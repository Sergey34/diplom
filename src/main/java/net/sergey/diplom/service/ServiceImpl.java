package net.sergey.diplom.service;

import com.google.gson.Gson;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    private final ApplicationContext applicationContext;
    private final DAO DAO;

    @Autowired
    public ServiceImpl(ApplicationContext applicationContext, DAO DAO) {
        this.applicationContext = applicationContext;
        this.DAO = DAO;
    }

    private static Settings jsonToObject(String jsonText) {
        return new Gson().fromJson(jsonText, Settings.class);
    }

    @Transactional
    @Override
    public void saveSettings(Profile filter) {
        //DAO.saveSettings(filter);
    }

    @Override
    public Settings loadSetting(String SQL) {
        return null;
    }

    @Override
    public String showData(String name) {
        return null;
    }


    @Override
    public Parser getBeanParserByName(String parserImplementation) {
        return applicationContext.getBean(parserImplementation, Parser.class);
    }

    @Override
    public Map<String, String> getMenu() throws IOException {
        Map<String, String> menu = new LinkedHashMap<>();
        Element body = Jsoup.connect("http://airfoiltools.com/").get().body();
        Elements menuList = body.getElementsByClass("mmenu").get(0).getElementsByTag("ul");

        for (Element menuElement : menuList) {
            Elements links = menuElement.getElementsByTag("li");
            for (Element link : links) {
                Element a = link.getElementsByTag("a").first();
                menu.put(a.text(), a.attr("href"));
            }

        }
        return menu;
    }

    private int createString(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    @Override
    public Object getAirfoilsByLiteral(String literal) throws IOException {
        Map<String, String> menu = getMenu();
        String url = menu.get(literal);
        String fullUrl = "http://airfoiltools.com/" + url.substring(0, url.length() - 1);
        int n = createString(Jsoup.connect(fullUrl + 0).get().html(), "Page 1 of ([0-9]+).+");
        for (int i = 0; i < n; i++) {
            Elements tr = Jsoup.connect(fullUrl + i).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr");
            System.out.println(i);
            for (int j = 0; j < tr.size(); j += 2) {
                Elements cell12 = tr.get(j).getElementsByClass("cell12");
                if (cell12.first() == null) {
                    j--;
                } else {
                    String name = cell12.text();
                    Elements links = tr.get(j).getElementsByClass("cell3");
                    String image = tr.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");
                    String description = tr.get(j + 1).getElementsByClass("cell2").text();

                    System.out.println(name);
                    System.out.println(links);
                    System.out.println(image);
                    System.out.println(description);
                }
            }

        }


        return null;
    }

    @Transactional
    @Override
    public List<User> getUser(String alex) {
        return DAO.getUserById(alex);
    }
}
