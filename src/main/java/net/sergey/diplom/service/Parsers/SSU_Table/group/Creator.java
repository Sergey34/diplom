package net.sergey.diplom.service.Parsers.SSU_Table.group;

import com.google.gson.Gson;
import net.sergey.diplom.service.Parsers.Hash;
import net.sergey.diplom.service.Parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(value = "net.schastny.contactmanager.service.Parsers.SSU_Table.faculty.Creator")
public class Creator implements Parser {
    private static final String SCHEDULE_PATTERN = "^<a href=\"/schedule/";
    private static final String SCHEDULE_MM = "http://www.sgu.ru/schedule/mm/";
    private static final String DO = "do";
    private static final String FIELDSET_WRAPPER = "fieldset-wrapper";
    private static final String END_TAG = "\">";
    private static final String A_END = "</a>";
    private static final String URL_PATTERN = "^.+do/(.+)\"";
    private static final char ENDL = '\n';

    private final Hash hash = Hash.INSTANCE;

    @Bean
    public Creator bean1() {
        return new Creator();
    }

    @Override
    public String getData(String jsonText) {
        Map<String, String> groupsMap = hash.getHashGroups();
        if (!groupsMap.isEmpty()) {
            return objectTojson(groupsMap);
        } else {
            return update("");
        }
    }

    @Override
    public String update(String jsonText) {
        Map<String, String> groupsMap = null;
        try {
            groupsMap = parseGroup();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (groupsMap != null && !groupsMap.isEmpty()) {
            hash.setHashGroups(groupsMap);
            return objectTojson(groupsMap);
        }
        return "{:}";
    }

    private String createString(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private Map<String, String> parseGroup() throws IOException {
        Map<String, String> groups = new HashMap<String, String>();
        Document doc = Jsoup.connect(SCHEDULE_MM).get();
        String groupsStr = doc.getElementsByClass(DO).html();
        doc = Jsoup.parse(groupsStr);
        groupsStr = doc.getElementsByClass(FIELDSET_WRAPPER).html();
        int end = groupsStr.indexOf(ENDL);
        while (end != -1) {
            String s;
            //получаем строку проверяем ее на то что она содержит номер группы и добавляем или пропускаем ее
            s = groupsStr.substring(0, end);
            Pattern pattern = Pattern.compile(SCHEDULE_PATTERN);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find() && s.length() > 10) {
                int k = s.indexOf(END_TAG) + 2;
                int b = s.indexOf(A_END);
                String title = s.substring(k, b);
                String groupURL = createString(s, URL_PATTERN);
                groups.put(title, groupURL);
            }
            groupsStr = groupsStr.substring(end + 1, groupsStr.length());
            end = groupsStr.indexOf(ENDL);
        }
        return groups;
    }

    private <T> String objectTojson(T obj) {
        return new Gson().toJson(obj);
    }
}
