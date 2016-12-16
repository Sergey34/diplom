package net.sergey.diplom.service.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.service.EventService;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sergey.diplom.service.parser.ConstantApi.*;
import static net.sergey.diplom.service.parser.ParserService.*;

@Scope("prototype")
@Component
public class ParserAirfoil implements Callable<Void> {
    private static final Pattern GET_ID_BY_FULL_NAME_PATTERN = Pattern.compile("\\(([a-zA-Z0-9_-]+)\\) .*");
    private static final Pattern GET_FILE_NAME_BY_URL_PATTERN = Pattern.compile("polar=(.+)$");
    private static final Pattern GET_COUNT_PAGES_PATTERN = Pattern.compile("Page 1 of ([0-9]+).+");
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final String REGEX = " +";
    private final DAO dao;
    private final EventService eventService;
    private String prefix;

    @Autowired
    public ParserAirfoil(DAO dao, EventService eventService) {
        this.dao = dao;
        this.eventService = eventService;
    }

    public static String csvToString(InputStream urlFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }

    @Override
    public Void call() throws Exception {
        parseAirfoilByUrl(prefix);
        return null;
    }

    private void parseAirfoilByUrl(String prefix) throws IOException {
        String url = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + prefix + "&no=";
        Prefix prefix1 = new Prefix(prefix.charAt(0));
        int countPages = createIntByPattern(getJsoupConnect(url, TIMEOUT).get().html(), GET_COUNT_PAGES_PATTERN);
        for (int i = 0; i < countPages; i++) {
            Elements airfoilList = getJsoupConnect(url + i, TIMEOUT).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr");
            List<Airfoil> airfoils = parsePage(prefix1, airfoilList, countPages);
            dao.addAirfoils(airfoils);
        }
        eventService.updateProgress(prefix, 100);
    }


    private List<Airfoil> parsePage(Prefix prefix1, Elements airfoilList, int countPages) throws IOException {
        List<Airfoil> airfoils = new ArrayList<>();
        for (int j = 0; j < airfoilList.size(); j += 2) {
            Elements cell12 = airfoilList.get(j).getElementsByClass("cell12");
            if (cell12.first() == null) {
                j--;//фильтруем реламу
            } else {
                String name = cell12.text();
                String description = airfoilList.get(j + 1).getElementsByClass("cell2").text();

                String idAirfoil = createStringByPattern(name, GET_ID_BY_FULL_NAME_PATTERN);
                Airfoil airfoil = new Airfoil(name, description, prefix1, idAirfoil);
                airfoil.setCoordView(parseCoordinateView(idAirfoil));
                airfoil.setCoordinates(downloadDetailInfo(idAirfoil));
                airfoils.add(airfoil);
                String key = String.valueOf(prefix1.getPrefix());
                eventService.updateProgress(key, eventService.getProgressValueByKey(key) + (90 / countPages / airfoilList.size()));
            }
        }
        return airfoils;
    }

    private String parseCoordinateView(String shortName) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new URL(GET_COORDINATE_VIEW + shortName).openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.trim().split(REGEX);
            if (isDoubleStr(split[0]) && isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(split[0]).append(",").append(split[split.length - 1]).append('\n');
            }
        }
        return stringBuilder.toString();
    }

    private int createIntByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private Set<Coordinates> downloadDetailInfo(String airfoil) throws IOException {
        Document detail = getJsoupConnect(GET_DETAILS + airfoil, TIMEOUT).get();
        Elements polar = detail.getElementsByClass("polar");
        if (polar.size() == 0) {
            return Collections.emptySet();
        }
        polar = polar.first().getElementsByTag("tr");
        Set<Coordinates> coordinates = new HashSet<>();
        for (Element element : polar) {
            Element reynolds = element.getElementsByClass("cell2").first();
            Element nCrit = element.getElementsByClass("cell3").first();
            Element maxClCd = element.getElementsByClass("cell4").first();
            Element cell7 = element.getElementsByClass("cell7").first();
            if (cell7 != null) {
                Elements a = cell7.getElementsByTag("a");
                if (a.size() != 0) {
                    String fileName = createStringByPattern(a.attr("href"), GET_FILE_NAME_BY_URL_PATTERN);
                    URL urlFile = new URL(GET_FILE_CSV + fileName);
                    LOGGER.debug("url {}{}", GET_FILE_CSV, fileName);
                    Coordinates coordinateItem = new Coordinates(csvToString(urlFile.openStream()), fileName + ".csv");
                    coordinateItem.setRenolgs(reynolds.text());
                    coordinateItem.setNCrit(nCrit.text());
                    coordinateItem.setMaxClCd(maxClCd.text());
                    coordinates.add(coordinateItem);
                }
            }
        }
        return coordinates;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
