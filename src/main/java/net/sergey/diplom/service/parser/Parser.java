package net.sergey.diplom.service.parser;

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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
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
    private static final Pattern GET_AIRFOIL_ID_BY_URL_PATTERN = Pattern.compile("airfoil=(.+)$");
    private static final Pattern GET_COUNT_PAGES_PATTERN = Pattern.compile("Page 1 of ([0-9]+).+");
    private static final Pattern GET_MENU_TITLE_PATTERN = Pattern.compile("^(.+) \\([0-9]*\\)$");

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final String HTTP_AIRFOIL_TOOLS_COM = "http://airfoiltools.com/";
    private final List<String> airfoilMenu = new ArrayList<>();
    private String PATH;
    @Autowired
    private DAO dao;

    public Parser(String path) {
        this.PATH = path;
    }

    public Parser() {
    }

    public void init() throws IOException {
        parseMenu();
        getAirfoilsByPrefix();
        initSimilar();
    }

    private void initSimilar() throws IOException {
        for (Airfoil airfoil : dao.getAllAirfoil()) {
            try {
                String idAirfoil = createStringByPattern(airfoil.getName(), GET_ID_BY_FULL_NAME_PATTERN);
                Document detail = Jsoup.connect(GET_DETAILS + idAirfoil).timeout(0).userAgent("Mozilla").ignoreHttpErrors(true).get();
                Elements similar = detail.getElementsByClass("similar").first().getElementsByTag("tr");
                for (Element similarItem : similar) {
                    String airfoilId = createStringByPattern(similarItem.getElementsByClass("c3").first().getElementsByTag("a").attr("href"), GET_AIRFOIL_ID_BY_URL_PATTERN);
                    Airfoil airfoilSimilar = dao.getAirfoilById(airfoilId.hashCode());
                    airfoil.addSimilar(airfoilSimilar);
                }
                LOGGER.info(airfoil.getName());
                dao.addAirfoil(airfoil);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void parseMenu() throws IOException {
        Element mmenu = Jsoup.connect(HTTP_AIRFOIL_TOOLS_COM).timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get().body().getElementsByClass("mmenu").get(0);
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
                break;
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

    private void getAirfoilsByPrefix() throws IOException {
        for (String url : airfoilMenu) {
            String fullUrl = ConstantApi.GET_LIST_AIRFOIL_BY_PREFIX + url;

            Prefix prefix1 = new Prefix(url.charAt(0));
            List<Airfoil> airfoils = new ArrayList<>();
            int countPages = createIntByPattern(Jsoup.connect(fullUrl).timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get().html(), GET_COUNT_PAGES_PATTERN);
            for (int i = 0; i < countPages; i++) {
                Elements airfoilList = Jsoup.connect(fullUrl + "&no=" + i).timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get().body().getElementsByClass("afSearchResult").
                        first().getElementsByTag("tr");
                parsePage(prefix1, airfoils, airfoilList);
            }
            dao.addAirfoil(airfoils);
        }
    }

    private void parsePage(Prefix prefix1, List<Airfoil> airfoils, Elements airfoilList) throws IOException {
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
            }
        }
    }

    private String parseCoordinateView(String shortName) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new URL("http://airfoiltools.com/airfoil/seligdatfile?airfoil=" + shortName).openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.trim().split(" ");
            if (isDoubleStr(split[0]) && isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(line.trim()).append('\n');
            }
        }
        return stringBuilder.toString();
    }

    private boolean isDoubleStr(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
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


    private Set<Coordinates> downloadDetailInfo(String airfoil) throws IOException {
        Document detail = Jsoup.connect(GET_DETAILS + airfoil).timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get();
        Elements polar1 = detail.getElementsByClass("polar");
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
                coordinates.add(new Coordinates(csvToString(urlFile), fileName));
            }
        }
        return coordinates;
    }


    private String csvToString(URL urlFile) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlFile.openStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }


    public Parser setPath(String path) {
        this.PATH = path;
        return this;
    }
}
