package net.sergey.diplom.service.Parsers.SSU_Table;


import com.google.gson.Gson;
import net.sergey.diplom.service.Parsers.Hash;
import net.sergey.diplom.service.Parsers.Parser;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static net.sergey.diplom.service.Parsers.ParserConstants.*;

@Service(value = "net.schastny.contactmanager.service.Parsers.SSU_Table.Creator")
public final class Creator implements Parser {
    private static final String PANES = "panes";
    private static final String L_R_1 = "l--r-1";
    private static final String L_R_3 = "l--r-3";
    private static final String L_DN = "l-dn";
    private static final String L_TN = "l-tn";
    private static final String L_P = "l-p";
    private static final String L_PR_T = "l-pr-t";
    private static final String L_PR_G = "l-pr-g";
    private static final String L_PR_R = "l-pr-r";
    private static final String L_R_2 = "l--r-2";
    private static final String SGU_SCHEDULE = "http://www.sgu.ru/schedule/";
    private static final String DO = "do";
    private static final String L_G = "l--g-";
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(UtilsLogger.getStaticClassName());
    private final String[] timeSubject = new String[]{"08:20-09:50", "10:00-11:35", "12:05-13:40", "13:50-15:25",
            "15:35-17:10", "17:20-18:40"};
    private final List<String> weekDay = new ArrayList<String>();
    private final Hash hash = Hash.INSTANCE;

    {
        weekDay.add(SPASE);
        weekDay.add("Понедельник");
        weekDay.add("Вторник");
        weekDay.add("Среда");
        weekDay.add("Четверг");
        weekDay.add("Пятница");
        weekDay.add("Суббота");
    }

    private String jsonToObject(String jsonText) {
        Group group = new Gson().fromJson(jsonText, Group.class);
        return "mm/" + DO + '/' + group.getGroup();
    }

    @Override
    public String getData(String jsonText) throws IOException {
        Map<String, ManySubject[][]> resultMap = new HashMap<String, ManySubject[][]>();
        String urlGroup = jsonToObject(jsonText);
        if (urlGroup != null) {
            Map<String, ManySubject[][]> hashTable = hash.getHashTable();
            if (hashTable.containsKey(urlGroup)) {
                resultMap.put(urlGroup, hashTable.get(urlGroup));
                return objectToTable(resultMap);
            } else {
                return update(jsonText);
            }
        }
        return "";
    }

    @Override
    public String update(String jsonText) throws IOException {
        String urlGroup = jsonToObject(jsonText);
        if (urlGroup != null) {
            Map<String, ManySubject[][]> result = new HashMap<>();
            ManySubject[][] tableJson = parsing(SGU_SCHEDULE + urlGroup);
            result.put(urlGroup, tableJson);
            hash.putAllToHashTable(result);
            return objectToTable(result);
        }
        return "";
    }

    private ManySubject[][] parsing(String URL) throws IOException {
        ManySubject[][] table = new ManySubject[6][6];
        String panes = Jsoup.connect(URL).get().body().getElementsByClass(PANES).html();
        Document doc = Jsoup.parse(panes);
        for (int i = 0; i < 6; i++) {
            table[i] = new ManySubject[6];
            for (int j = 0; j < 6; j++) {
                table[i][j] = new ManySubject();
                String index = (i + 1) + "_" + (j + 1);
                Element elementById = doc.body().getElementById(index);
                if (elementById == null) {
                    return null;
                }
                String subjectItem = elementById.html();//в каждую ячейку получили html ячейки с предметом
                Document html = Jsoup.parse(subjectItem);
                if (parseManyGroup(i, j, html, table)) {
                    pushTable(i, j, html, subjectItem, table);//заполняем предметы без деления на группы
                }
            }
        }
        return table;
    }

    private boolean parseManyGroup(int i, int j, final Document html, ManySubject[][] table) {
        if (isManyGroup(html)) {
            for (int k = 0; k < 20; k++) {
                String manySubject = html.body().getElementsByClass(L_G + k).html();
                if (manySubject.length() != 0) {
                    Document manySubjectHtml = Jsoup.parse(manySubject);
                    Subject subject = new Subject();
                    subject.setTitle(manySubjectHtml.body().getElementsByClass(L_DN).text());
                    subject.setName(manySubjectHtml.body().getElementsByClass(L_TN).text());
                    subject.setNumberGroup(getType(manySubjectHtml));
                    subject.setAudit(manySubjectHtml.body().getElementsByClass(L_P).text());
                    subject.setLecPr(manySubjectHtml.body().getElementsByClass(L_PR_T).text());
                    table[i][j].addSubject(subject);
                    if (table[i][j].getSubject().size() > 1) {
                        table[i][j].setF();
                    }
                }
            }
            return false;
        }
        return true;
    }

    private void pushTable(int i, int j, Document html, String subjectItem, ManySubject[][] table) {
        html = Jsoup.parse(html.body().getElementsByClass(L_R_1).html());//получили общий предмет из ячейки
        Subject subject;
        if (html.getAllElements().text().length() != 0 && !table[i][j].isF()) {
            subject = new Subject();
            pushTableBaza(i, j, html, subject, table);
        } else {
            subject = new Subject();
            html = Jsoup.parse(subjectItem);
            pushTableChis(html, subject);
            pushTableZnam(html, subject);
            table[i][j].addSubject(subject);
        }
    }

    private void pushTableBaza(int i, int j, Document html, Subject subject, ManySubject[][] table) {
        table[i][j].getSubject().clear();
        subject.setTitle(filterRepetition(html.body().getElementsByClass(L_DN).text()));
        subject.setAudit(filterRepetition(html.body().getElementsByClass(L_P).text()));
        subject.setLecPr(filterRepetition(html.body().getElementsByClass(L_PR_T).text()));
        subject.setName(html.body().getElementsByClass(L_TN).text());
        subject.setNumberGroup(getType(html));
        table[i][j].addSubject(subject);
    }

    private void pushTableChis(Document html, Subject subject) {
        String chisl = html.body().getElementsByClass(L_R_2).html();//получили предмет в числителе
        Document chZn = Jsoup.parse(chisl);
        subject.setCHtitle(filterRepetition(chZn.body().getElementsByClass(L_DN).text()));
        subject.setCHaudit(filterRepetition(chZn.body().getElementsByClass(L_P).text()));
        subject.setCHchisZnam(filterRepetition(chZn.body().getElementsByClass(L_PR_R).text()));
        subject.setCHlecPr(filterRepetition(chZn.body().getElementsByClass(L_PR_T).text()));
        subject.setCHname(chZn.body().getElementsByClass(L_TN).text());
        subject.setCHtype(getType(chZn));
    }

    private void pushTableZnam(Document html, Subject subject) {
        String znam = html.body().getElementsByClass(L_R_3).html();//получили предмет в знаменателе
        Document chZn = Jsoup.parse(znam);
        subject.setZtitle(filterRepetition(chZn.body().getElementsByClass(L_DN).text()));
        subject.setZaudit(filterRepetition(chZn.body().getElementsByClass(L_P).text()));
        subject.setZchisZnam(filterRepetition(chZn.body().getElementsByClass(L_PR_R).text()));
        subject.setZlecPr(filterRepetition(chZn.body().getElementsByClass(L_PR_T).text()));
        subject.setZname(chZn.body().getElementsByClass(L_TN).text());
        subject.setZtype(getType(chZn));
    }

    private String filterRepetition(String str) {
        String mas[] = str.split(SPASE);
        String result = removeDuplicates(mas);
        return result.trim();
    }

    private String removeDuplicates(String[] values) {
        boolean mask[] = new boolean[values.length];
        int removeCount = 0;
        for (int i = 0; i < values.length; i++) {
            if (!mask[i]) {
                String tmp = values[i];
                for (int j = i + 1; j < values.length; j++) {
                    if (tmp.equals(values[j])) {
                        mask[j] = true;
                        removeCount++;
                    }
                }
            }
        }
        String[] result = new String[values.length - removeCount];
        for (int i = 0, j = 0; i < values.length; i++) {
            if (!mask[i]) {
                result[j++] = values[i];
            }
        }
        String rez = "";
        for (String aResult : result) {
            rez += aResult + SPASE;
        }
        return rez;
    }

    private boolean isManyGroup(Document html) {
        String manyGroupSubjectAll = html.body().getElementsByClass("l--t-6").html();
        String[] manyGroupSubjects = manyGroupSubjectAll.split("\n");
        int k = 0;
        for (String manyGroupSubject : manyGroupSubjects) {
            if (manyGroupSubject.trim().equals("<div class=\"l-pr\">")) {
                k++;
            }
        }
        String s2 = html.body().getElementsByClass("l--r-2").html();
        String s3 = html.body().getElementsByClass("l--r-3").html();
        return k > 1 && s2.length() == 0 && s3.length() == 0;
    }

    private String getType(Document html) {
        return html.body().getElementsByClass(L_PR_G).text();
    }

    private String objectToTable(Map<String, ManySubject[][]> table) {
        StringBuffer result = new StringBuffer(TABLE_HEAD + CAPTION);
        for (Entry<String, ManySubject[][]> entry : table.entrySet()) {
            ManySubject[][] tableMas = entry.getValue();
            result.append(entry.getKey()).append(TABLE_BODY);
            createColumnHeadings(weekDay, result);
            for (int i = 0; i < tableMas.length; i++) {
                result.append(TH).append(timeSubject[i]).append(TH_END);
                for (int j = 0; j < tableMas[i].length; j++) {
                    List<Subject> subjects = tableMas[i][j].getSubject();
                    result.append(TH);
                    for (Subject subject : subjects) {
                        if (subject.getTitle().length() != 0) {
                            fillBase(result, subject);
                        } else {
                            fillNumerator(result, subject);
                            fillDenominator(result, subject);
                        }
                    }
                    result.append(TH_END);
                }
                result.append(TR_END);
            }
            result.append(TABLE_END_BODY);
        }
        return result.toString();
    }

    private void fillDenominator(StringBuffer result, Subject subject) {
        if (subject.getZtitle().length() != 0) {
            result.append(BR).append(subject.getZtype()).append(SPASE).append(subject.getZtitle())
                    .append(BR).append(subject.getZlecPr()).append(SPASE).
                    append(subject.getZchisZnam()).append(EMSP).append(subject.getZaudit())
                    .append(BR).append(subject.getZname());
        }
    }

    private void fillNumerator(StringBuffer result, Subject subject) {
        if (subject.getCHtitle().length() != 0) {
            result.append(subject.getCHtype()).append(SPASE).append(subject.getCHtitle()).append(BR)
                    .append(subject.getCHlecPr()).append(SPASE).append(subject.getCHchisZnam())
                    .append(EMSP).append(subject.getCHaudit()).append(BR).append(subject.getCHname())
                    .append(BR);
        }
    }

    private void fillBase(StringBuffer result, Subject subject) {
        result.append(subject.getNumberGroup()).append(SPASE).append(subject.getTitle()).append(BR)
                .append(subject.getLecPr()).append(EMSP).append(subject.getAudit()).append(BR).append(subject.getName())
                .append(BR);
    }

    private void createColumnHeadings(List<String> weekDay, StringBuffer result) {
        result.append(TR);
        for (String aWeekDay : weekDay) {
            result.append(TH).append(aWeekDay).append(TH_END);
        }
        result.append(TR_END);
    }

    public void initIt() throws IOException {
        logger.info("заполняется кеш SSU_Table...");
        Parser creator = new net.sergey.diplom.service.Parsers.SSU_Table.group.Creator();
        String data = creator.getData("");
        List<Group> groupList = jsonMapToList(data);
        try {
            for (Group group : groupList) {
                update(objectToJson(group));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            logger.info("SSU_Table Завершено");
        }
    }

    private <T> String objectToJson(T object) {
        return new Gson().toJson(object);
    }

    private List<Group> jsonMapToList(String jsonText) {
        List<String> groupsStringList = new ArrayList<String>(new Gson().fromJson(jsonText, Map.class).values());
        List<Group> groupsList = new ArrayList<Group>();
        for (String groupString : groupsStringList) {
            groupsList.add(new Group(groupString));
        }
        return groupsList;
    }

}