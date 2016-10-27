package net.sergey.diplom.service;

import com.google.gson.Gson;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.MenuItem;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import net.sergey.diplom.service.utils.UtilParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    public static final String HTTP_AIRFOILTOOLS_COM = "http://airfoiltools.com/";
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
    public List<Menu> getMenu() throws IOException {
        Element mmenu = Jsoup.connect(HTTP_AIRFOILTOOLS_COM).get().body().getElementsByClass("mmenu").get(0);
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        ArrayList<Menu> menus = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Menu menu1 = new Menu();
            Elements element = headerMenu.get(i).getElementsByTag("h3");
            menu1.setHeader(element.text());
            Set<MenuItem> menuItems = new LinkedHashSet<>();
            Elements links = menuElement.getElementsByTag("li");
            for (Element link : links) {
                Element a = link.getElementsByTag("a").first();
                menuItems.add(new MenuItem(a.text(), a.attr("href")));
            }
            menu1.setMenuItems(menuItems);
            menus.add(menu1);
        }
        return menus;
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
        List<Menu> menu = getMenu();
        String url = UtilParser.getUrlMenuByTitle(menu, literal);
        String fullUrl = HTTP_AIRFOILTOOLS_COM + url.substring(0, url.length() - 1);
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

    @Override
    public List<User> getUser(String alex) {
        return DAO.getUserById(alex);
    }
}
