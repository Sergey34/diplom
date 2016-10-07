package net.sergey.diplom.service.Parsers.homeCinema;

import com.google.gson.Gson;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Hash;
import net.sergey.diplom.service.Parsers.Parser;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sergey.diplom.service.Parsers.ParserConstants.*;

/**
 * Created by sergey on 10.02.16.
 */
@Service(value = "net.schastny.contactmanager.service.Parsers.homeCinema.Creator")
public final class Creator implements Parser {
    private static final String DOMKINOSAR_AFISHA = "http://www.domkinosar.ru/afisha.html?date=";
    private static final String MOVIE_CONTENT_PATTERN = "^<br> <u>(.+) <a href";
    private static final String KEY_PATTERN = "^<b>([А-я]+:)</b>";
    private static final String HALL_PATTERN = " \\(([А-Я ]+)\\)$";
    private static final String MOVIE_TITLE_PATTERN = "^(.+) \\(";
    private static final String INFO_PATTERN = "</b> (.+)$";
    private static final String DESCRIPTION = "Описание:";
    private static final String LEFT_BLOCK = "left_block";
    private static final String DIV_CLASS_BOX = "<div class=\"box\"> ";
    private static final String DIV_CLASS_BOX_CLOSED = "<div class=\"box closed\"> ";
    private static final String BR = " <br> ";
    private static final String DESCRIPTION_FILM = "description_film";
    private static final String U = "u";
    private static final String I = "i";
    private static final String TOP_BOX_PARENT = "top_box_parent";
    private Hash hash = Hash.INSTANCE;

    private Map<String, Integer> weekDayToData = new HashMap<String, Integer>();

    {
        weekDayToData.put("Понедельник", 2);
        weekDayToData.put("Вторник", 3);
        weekDayToData.put("Среда", 4);
        weekDayToData.put("Четверг", 5);
        weekDayToData.put("Пятница", 6);
        weekDayToData.put("Суббота", 7);
        weekDayToData.put("Воскресенье", 1);
    }

    @Override
    public String getData(String jsonText) throws IOException {
        List<String> weekDays = jsonToObject(jsonText);
        Map<String, List<Film>> result = new LinkedHashMap<String, List<Film>>();
        Map<String, List<Film>> hashCinema = hash.getHashCinema();
        for (String weekDay : weekDays) {
            if (hashCinema.containsKey(weekDay)) {
                result.put(weekDay, hashCinema.get(weekDay));
            } else {
                result.putAll(updateDataNotFound(jsonText));
            }
        }
        return objectToTable(result);
    }

    @Override
    public String update(String jsonText) throws IOException {
        Map<String, List<Film>> result = reloadData(jsonText);
        return objectToTable(result);
    }

    private Map<String, List<Film>> updateDataNotFound(String jsonText) throws IOException {
        return reloadData(jsonText);
    }

    private Map<String, List<Film>> reloadData(String jsonText) throws IOException {
        List<String> weekDays = jsonToObject(jsonText);
        List<String> dates = getNearestDateList(weekDays);
        Map<String, List<Film>> result = new HashMap<String, List<Film>>();
        for (int i = 0; i < dates.size(); i++) {
            List<Film> listFilm = parsing(DOMKINOSAR_AFISHA + dates.get(i));
            result.put(weekDays.get(i), listFilm);
        }
        hash.putAllToHashCinema(result);
        return result;
    }

    private List<Film> parsing(String URL) throws IOException {
        Document doc = Jsoup.connect(URL).get();
        String content = doc.body().getElementsByClass(LEFT_BLOCK).html();
        String[] box = content.split(DIV_CLASS_BOX);
        String[] boxClosed = box[box.length - 1].split(DIV_CLASS_BOX_CLOSED);

        if (box.length > 1) {
            List<Film> filmList = createListFilm(box, 1, box.length - 1);
            filmList.addAll(createListFilm(boxClosed, 0, boxClosed.length));
            return filmList;
        }
        return new ArrayList<Film>();
    }

    private List<Film> createListFilm(String[] box, int start, int size) {
        List<Film> listFilm = new ArrayList<Film>();
        for (int i = start; i < size; i++) {
            Film film = new Film();
            String title = Jsoup.parse(box[i]).body().getElementsByClass(TOP_BOX_PARENT).html();
            film.setTime(Jsoup.parse(title).body().getElementsByTag(I).text());
            title = Jsoup.parse(title).body().getElementsByTag(U).text();
            String hall = createString(title, HALL_PATTERN);
            String movieTitle = createString(title, MOVIE_TITLE_PATTERN + hall);
            String descriptionFilm = Jsoup.parse(box[i]).body().getElementsByClass(DESCRIPTION_FILM).html();
            film.setHall(hall);
            film.setMovieTitle(movieTitle);
            String[] mas = descriptionFilm.split(BR);
            for (int j = 0; j < mas.length - 1; j++) {
                String key = createString(mas[j], KEY_PATTERN);
                film.addFilmInfo(key, createString(mas[j], key + INFO_PATTERN));
            }
            film.addFilmInfo(DESCRIPTION, createString(mas[mas.length - 1], MOVIE_CONTENT_PATTERN));
            listFilm.add(film);
        }
        return listFilm;
    }

    private String createString(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private String objectToTable(Map<String, List<Film>> poster) {
        StringBuilder result = new StringBuilder(TABLE_HEAD).append(CAPTION);
        result.append("АФИША").append(TABLE_BODY).append(TR);
        for (Map.Entry<String, List<Film>> entry : poster.entrySet()) {
            String key = entry.getKey();//дни недели
            List<Film> posterItem = entry.getValue();//сама афиша
            StringBuffer posterOnWeekDay = createPosterOnWeekDay(key, posterItem);
            if (posterOnWeekDay != null) {
                result.append(TD).append(posterOnWeekDay).append(TD_END);
            }
        }
        result.append(TR_END).append(TABLE_END_BODY);
        return result.toString();
    }

    private StringBuffer createPosterOnWeekDay(String key, List<Film> posterItem) {
        if (posterItem.size() != 0) {
            StringBuffer str = new StringBuffer(TABLE_HEAD);
            str.append(TBODY).append(TR).append(TH).append(key).append(TR_END).append(TH_END);
            for (Film posterDay : posterItem) {
                str.append(TR).append(TD);
                str.append(posterDay.getMovieTitle()).append(SPASE).append(posterDay.getTime()).append(BR).
                        append(posterDay.getHall()).append(BR);
                StringBuilder mapToString = new StringBuilder(TABLE_SUB).append(TBODY);
                for (Map.Entry<String, String> entry : posterDay.getFilmInfo().entrySet()) {
                    mapToString.append(TR);
                    mapToString.append(TD).append(entry.getKey()).append(TD_END);
                    mapToString.append(TD).append(entry.getValue()).append(TD_END);
                    mapToString.append(TR_END);
                }
                mapToString.append(TR_END).append(TABLE_END_BODY);
                str.append(mapToString);
                str.append(TR_END).append(TD_END);
            }
            str.append(TABLE_END_BODY);
            return str;
        }
        return null;
    }

    private List<String> getNearestDateList(List<String> weekDays) {
        List<String> dates = new ArrayList<String>();
        for (String days : weekDays) {
            dates.add(getNearestDate(days));
        }
        return dates;
    }

    private String getNearestDate(String item) {
        Calendar calendar = Calendar.getInstance();
        int nowWeekDayNumber = calendar.get(Calendar.DAY_OF_WEEK);
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
        Date date = new Date();
        int weekDayNumber = weekDayToData.get(item);
        int daysLeftUntilWeekDay;
        if (nowWeekDayNumber > weekDayNumber) {
            daysLeftUntilWeekDay = 7 + (weekDayNumber - nowWeekDayNumber);
        } else {
            daysLeftUntilWeekDay = Math.abs(nowWeekDayNumber - weekDayNumber);
        }
        Long time = date.getTime();
        time = time + (60 * 60 * 24 * 1000 * daysLeftUntilWeekDay);
        date = new Date(time);
        return df.format(date);
    }

    private List<String> jsonToObject(String jsonText) {
        Settings setting = new Gson().fromJson(jsonText, Settings.class);
        List<String> listWeekDay = new ArrayList<String>();
        if (setting.isMonday()) {
            listWeekDay.add("Понедельник");
        }
        if (setting.isTuesday()) {
            listWeekDay.add("Вторник");
        }
        if (setting.isWednesday()) {
            listWeekDay.add("Среда");
        }
        if (setting.isThursday()) {
            listWeekDay.add("Четверг");
        }
        if (setting.isFriday()) {
            listWeekDay.add("Пятница");
        }
        if (setting.isSaturday()) {
            listWeekDay.add("Суббота");
        }
        if (setting.isSunday()) {
            listWeekDay.add("Воскресенье");
        }
        return listWeekDay;
    }

    private static final Logger logger = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    public void initIt() {
        logger.info("заполняется кеш Cinema...");
        String filter = "{\"Monday\":true,\"Tuesday\":true,\"Wednesday\":true,\"Thursday\":true,\"Friday\":true," +
                "\"Saturday\":true,\"Sunday\":true}";
        try {
            update(filter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.info("Cinema Завершено");
        }
    }
}
