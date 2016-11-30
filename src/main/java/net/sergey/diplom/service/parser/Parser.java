package net.sergey.diplom.service.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.sergey.diplom.service.ConstantApi.GET_DETAILS;

@Component
public class Parser {
    private static final Pattern GET_ID_BY_FULL_NAME_PATTERN = Pattern.compile("\\(([a-zA-Z0-9_-]+)\\) .*");
    private static final Pattern GET_AIRFOIL_ID_BY_URL_PATTERN = Pattern.compile("airfoil=(.+)$");
    private static final Pattern GET_MENU_TITLE_PATTERN = Pattern.compile("^(.+) \\([0-9]*\\)$");
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final String HTTP_AIRFOIL_TOOLS_COM = "http://airfoiltools.com/";

    @Autowired
    private DAO dao;

    public Parser() {
    }

    public void init() throws Exception {
        List<String> menu = parseMenu();
        getAirfoilsByMenuList(menu);
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
                    if (airfoilSimilar != null) {
                        airfoil.addSimilar(airfoilSimilar);
                    }
                }
                LOGGER.info(airfoil.getName());
                dao.addAirfoil(airfoil);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    private List<String> parseMenu() throws IOException {
        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = Jsoup.connect(HTTP_AIRFOIL_TOOLS_COM).timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get().body().getElementsByClass("mmenu").get(0);
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        List<Menu> menus = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Elements element = headerMenu.get(i).getElementsByTag("h3");
            if ("Airfoils A to Z".equals(element.text())) {
                Menu menu1 = new Menu(element.text());
                List<MenuItem> menuItems = new ArrayList<>();
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
        } catch (ConstraintViolationException e) {
            LOGGER.warn("Элемент меню: {} \n уже существует в базе {}", menus, e.getStackTrace());
            throw e;
        }
        return airfoilMenu;
    }


    private String createPrefix(String text) {
        if (!"".equals(text)) {
            return String.valueOf(text.charAt(0));
        } else {
            return "allAirfoil";
        }
    }

    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private void getAirfoilsByMenuList(List<String> prefixList) throws IOException, InterruptedException, ExecutionException {
        Collection<Future<List<Airfoil>>> futureList = new ArrayList<>();
        for (String prefix : prefixList) {
            Future<List<Airfoil>> submit = executorService.submit(new ParserAirfoil(prefix));
            futureList.add(submit);
        }
        for (Future<List<Airfoil>> listFuture : futureList) {
            List<Airfoil> airfoils = listFuture.get();
            dao.addAirfoils(airfoils);
        }
    }

    private String createStringByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public String csvToString(InputStream urlFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlFile))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            return stringBuilder.toString();
        }
    }
}
