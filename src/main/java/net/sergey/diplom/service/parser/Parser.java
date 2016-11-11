package net.sergey.diplom.service.parser;

import au.com.bytecode.opencsv.CSVReader;
import com.google.gson.Gson;
import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.service.ConstantApi;
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
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sergey.diplom.service.ConstantApi.GET_DETAILS;
import static net.sergey.diplom.service.ConstantApi.GET_FILE_CSV;

@Component
public class Parser {
    private static final Pattern GET_ID_BY_FULL_NAME_PATTERN = Pattern.compile("\\(([a-zA-Z0-9_-]+)\\) .*");
    private static final Pattern GET_FILE_NAME_BY_URL_PATTERN = Pattern.compile("polar=(.+)$");
    private static final Pattern GET_COUNT_PAGES_PATTERN = Pattern.compile("Page 1 of ([0-9]+).+");
    private static final Pattern GET_MENU_TITLE_PATTERN = Pattern.compile("^(.+) \\([0-9]*\\)$");
    @Autowired
    DAO dao;
    @Autowired
    private ServletContext servletContext;

    private List<String> airfoilMenu = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    private static final String HTTP_AIRFOIL_TOOLS_COM = "http://airfoiltools.com/";

    public void init() throws IOException {
        parseMenu();
        getAirfoilsByPrefix();
    }

    public void parseMenu() throws IOException {
        Element mmenu = Jsoup.connect(HTTP_AIRFOIL_TOOLS_COM).get().body().getElementsByClass("mmenu").get(0);
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        List<Menu> menus = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Elements element = headerMenu.get(i).getElementsByTag("h3");
            if ("Airfoils A to Z".equals(element.text())) {
                Menu menu1 = new Menu(element.text());
                Set<MenuItem> menuItems = new LinkedHashSet<>();
                Elements links = menuElement.getElementsByTag("li");

                for (Element link : links) {
                    Element a = link.getElementsByTag("a").first();
                    if (menu1.getHeader().equals("Airfoils A to Z")) {
                        String text = createStringByPattern(a.text(), GET_MENU_TITLE_PATTERN);
                        String prefix = createPrefix(text);
                        MenuItem menuItem = new MenuItem(text, prefix);

                        if (!prefix.equals("allAirfoil")) {
                            airfoilMenu.add(prefix);
                            menuItems.add(menuItem);
                        }

                    }
                }
                menu1.setMenuItems(menuItems);
                menus.add(menu1);
            }
        }

        try {
            dao.addMenus(menus);
//            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Элемент меню: {} \n уже существует в базе {}", menus, e.getStackTrace());
            throw e;
//            return false;
        }
    }


    private String createPrefix(String text) {
        if (!"".equals(text)) {
            return String.valueOf(text.charAt(0));
        } else {
            return "allAirfoil";
        }
    }

    public void getAirfoilsByPrefix() throws IOException {
        for (String url : airfoilMenu) {
            String fullUrl = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + url;

            Prefix prefix1 = new Prefix(url.charAt(0));
            List<Airfoil> airfoils = new ArrayList<>();
            int countPages = createIntByPattern(Jsoup.connect(fullUrl).get().html(), GET_COUNT_PAGES_PATTERN);
            for (int i = 0; i < countPages; i++) {
                Elements airfoilList = Jsoup.connect(fullUrl + "&no=" + i).get().body().getElementsByClass("afSearchResult").
                        first().getElementsByTag("tr");
                parsePage(prefix1, airfoils, airfoilList);
            }
            dao.addAirfoils(airfoils);
        }
    }

    private void parsePage(Prefix prefix1, List<Airfoil> airfoils, Elements airfoilList) throws IOException {
        for (int j = 0; j < airfoilList.size(); j += 2) {
            Elements cell12 = airfoilList.get(j).getElementsByClass("cell12");
            if (cell12.first() == null) {
                j--;//фильтруем реламу
            } else {
                String name = cell12.text();
                String image = downloadImage(airfoilList, j);
                String description = airfoilList.get(j + 1).getElementsByClass("cell2").text();

                String idAirfoil = createStringByPattern(name, GET_ID_BY_FULL_NAME_PATTERN);
                Airfoil airfoil = new Airfoil(name, description, image, prefix1, idAirfoil);
                airfoil.setCoordinates(downloadDetailInfo(idAirfoil));
                airfoils.add(airfoil);
            }
        }
    }

    private String downloadImage(Elements airfoilList, int j) throws IOException {
        String imgUrl = airfoilList.get(j + 1).getElementsByClass("cell1").first().getElementsByTag("a").attr("href");

        String path = servletContext.getRealPath("/resources/airfoil_img/");
        BufferedImage img = ImageIO.read(new URL(HTTP_AIRFOIL_TOOLS_COM + imgUrl));
        File file = new File(path + imgUrl);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("не создался");
            }
        }
        ImageIO.write(img, "png", file);
        return imgUrl;
    }

    private String createStringByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private int createIntByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }


    public Set<Coordinates> downloadDetailInfo(String airfoil) throws IOException {
        Elements polar1 = Jsoup.connect(GET_DETAILS + airfoil).get().getElementsByClass("polar");
        if (polar1.size() == 0) {
            return Collections.emptySet();
        }
        Elements polar = polar1.first().getElementsByClass("cell7");
        Set<Coordinates> coordinates = new HashSet<>();
        for (Element element : polar) {
            Elements a = element.getElementsByTag("a");
            if (a.size() != 0) {
                String fileName = createStringByPattern(a.attr("href"), GET_FILE_NAME_BY_URL_PATTERN);
                URL urlFile = new URL(GET_FILE_CSV + fileName);
                LOGGER.debug("url {}{}", GET_FILE_CSV, fileName);
                coordinates.add(new Coordinates(parseFileCSVtoJson(urlFile), fileName));
            }
        }
        return coordinates;
    }

    private String parseFileCSVtoJson(URL urlFile) throws IOException {
        Map<String, List<Double>> coordinates;
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(urlFile.openStream()), ',')) {
            List<String[]> strings = csvReader.readAll();
            coordinates = generateMapping(strings.get(10));
            int count = strings.get(10).length;
            for (int i = 0; i < count; i++) {
                String key = strings.get(10)[i];
                for (int j = 11; j < strings.size(); j++) {
                    coordinates.get(key).add(Double.parseDouble(strings.get(j)[i]));
                }
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn("невалидный файл!!! {}\n{}", urlFile, e.getMessage());
            return new Gson().toJson(null);
        }
        return new Gson().toJson(coordinates);
    }

    private Map<String, List<Double>> generateMapping(String[] keys) {
        HashMap<String, List<Double>> coordinates = new HashMap<>();
        for (String key : keys) {
            coordinates.put(key, new ArrayList<>());
        }
        return coordinates;
    }

}
