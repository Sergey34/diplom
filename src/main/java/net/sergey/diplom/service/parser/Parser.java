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

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Parser {
    private static final Pattern GET_PREFIX_BY_URL_PATTERN = Pattern.compile("page=(.)");
    private static final Pattern GET_COUNT_PAGES_PATTERN = Pattern.compile("Page 1 of ([0-9]+).+");
    @Autowired
    DAO dao;
    @Autowired
    private ServletContext servletContext;

    List<String> airfoilMenu = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    private static final String HTTP_AIRFOILTOOLS_COM = "http://airfoiltools.com/";

    public void init() throws IOException {
        parseMenu();
        getAirfoilsByPrefix();
    }

    public void parseMenu() throws IOException {
        Element mmenu = Jsoup.connect(HTTP_AIRFOILTOOLS_COM).get().body().getElementsByClass("mmenu").get(0);
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        List<Menu> menus = new ArrayList<>();
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
                    String text = a.text();//// TODO: 05.11.16 выделить префикс, удалить количество
                    String prefix = createPrefix(text);
                    String urlAction = "Javascript:getContent('" + prefix + "')";
                    menuItem = new MenuItem(text, urlAction);

                    if (!"List of all airfoils".equals(text)) {
                        airfoilMenu.add(a.attr("href"));
                    }
                } else {
                    menuItem = new MenuItem(a.text(), a.attr("href"));
                }
                menuItems.add(menuItem);
            }
            menu1.setMenuItems(menuItems);
            menus.add(menu1);
        }

        try {
            //dao.addMenus(menus);
//            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Элемент меню: {} \n уже существует в базе {}", menus, e.getStackTrace());
            throw e;
//            return false;
        }
    }


    private String createPrefix(String text) {
        if (!"List of all airfoils".equals(text)) {
            return String.valueOf(text.charAt(0));
        } else {
            return "allAirfoil";
        }
    }

    public void getAirfoilsByPrefix() throws IOException {
        for (String url : airfoilMenu) {
            String fullUrl = HTTP_AIRFOILTOOLS_COM + url;

            Prefix prefix1 = new Prefix(createCharByPattern(url, GET_PREFIX_BY_URL_PATTERN));
            List<Airfoil> airfoils = new ArrayList<>();
            int countPages = createIntByPattern(Jsoup.connect(fullUrl + 0).get().html(), GET_COUNT_PAGES_PATTERN);
            for (int i = 0; i < countPages; i++) {
                Elements airfoilList = Jsoup.connect(fullUrl + i).get().body().getElementsByClass("afSearchResult").
                        first().getElementsByTag("tr");
                parsePage(prefix1, airfoils, airfoilList);
            }
            //dao.addAirfoils(airfoils);
        }

        //// TODO: 05.11.16 если положили успешно обновить значение count для соответствующей строки в таблице menuItem

    }

    private void parsePage(Prefix prefix1, List<Airfoil> airfoils, Elements airfoilList) throws IOException {
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

    private String downloadImage(Elements airfoilList, int j) throws IOException {
        String imgUrl = airfoilList.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");

        String path = servletContext.getRealPath("/resources/airfoil_img/");
        BufferedImage img = ImageIO.read(new URL(HTTP_AIRFOILTOOLS_COM + imgUrl));
        File file = new File(path + imgUrl);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("не создался");
            }
        }
        ImageIO.write(img, "png", file);
        //// TODO: 05.11.16 скачать картинку
        return imgUrl;
    }


    private char createCharByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1).charAt(0);
        }
        return '0';
    }

    private int createIntByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }


}
