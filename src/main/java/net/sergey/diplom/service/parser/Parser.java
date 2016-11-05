package net.sergey.diplom.service.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Parser {
    @Autowired
    DAO dao;


    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    private static final String HTTP_AIRFOILTOOLS_COM = "http://airfoiltools.com/";

    private void parseMenu() throws IOException {
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
                MenuItem menuItem;
                if (menu1.getHeader().equals("Airfoils A to Z")) {
                    String urlAction = a.attr("href");//// TODO: 05.11.16 сделать строку вызывающую js
                    String text = a.text();//// TODO: 05.11.16 выделить префикс, удалить количество
                    menuItem = new MenuItem(text, urlAction);
                } else {
                    menuItem = new MenuItem(a.text(), a.attr("href"));
                }
                menuItems.add(menuItem);
            }
            menu1.setMenuItems(menuItems);
            menus.add(menu1);
        }

        try {
            dao.addMenus(menus);
//            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Элемент меню: {} \n уже существует в базе {}", menus, e.getStackTrace());
//            return false;
        }
    }


    public List<Airfoil> getAirfoilsByPrefix(String prefix) throws IOException {
        List<Menu> menu = dao.getAllMenu();
        String url = UtilParser.getUrlMenuByTitle(menu, prefix);
        String fullUrl = HTTP_AIRFOILTOOLS_COM + url.substring(0, url.length() - 1);

        Prefix prefix1 = new Prefix(prefix.charAt(0));
        List<Airfoil> airfoils = new ArrayList<>();
        int countPages = getCountPages(Jsoup.connect(fullUrl + 0).get().html(), "Page 1 of ([0-9]+).+");
        for (int i = 0; i < countPages; i++) {
            Elements airfoilList = Jsoup.connect(fullUrl + i).get().body().getElementsByClass("afSearchResult").
                    first().getElementsByTag("tr");
            parsePage(prefix1, airfoils, airfoilList);
        }
        dao.addAirfoils(airfoils);
        //// TODO: 05.11.16 если положили успешно обновить значение count для соответствующей строки в таблице menuItem
        return airfoils;
    }

    private void parsePage(Prefix prefix1, List<Airfoil> airfoils, Elements airfoilList) {
        for (int j = 0; j < airfoilList.size(); j += 2) {
            Elements cell12 = airfoilList.get(j).getElementsByClass("cell12");
            if (cell12.first() == null) {
                j--;//фильтруем реламу
            } else {
                String name = cell12.text();
                Elements links = airfoilList.get(j).getElementsByClass("cell3");
                String image = downloadImage(airfoilList, j);
                String description = airfoilList.get(j + 1).getElementsByClass("cell2").text();

                Airfoil airfoil = new Airfoil(name, description, image, prefix1);
                airfoil.setLinks(UtilParser.parseLinks(links));
                airfoils.add(airfoil);
            }
        }
    }

    private String downloadImage(Elements airfoilList, int j) {
        String imgUrl = airfoilList.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");
        //// TODO: 05.11.16 скачать картинку
        return imgUrl;
    }

    private int getCountPages(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }


}
