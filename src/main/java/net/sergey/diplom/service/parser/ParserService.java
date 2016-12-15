package net.sergey.diplom.service.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.service.EventService;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserService {
    static final int TIMEOUT = 10_000;
    private static final Pattern GET_MENU_TITLE_PATTERN = Pattern.compile("^(.+) \\([0-9]*\\)$");
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final String HTTP_AIRFOIL_TOOLS_COM = "http://airfoiltools.com/";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ApplicationContext applicationContext;
    private final DAO dao;
    private final EventService eventService;

    @Autowired
    public ParserService(ApplicationContext applicationContext, DAO dao, EventService eventService) {
        this.applicationContext = applicationContext;
        this.dao = dao;
        this.eventService = eventService;
    }

    static Connection getJsoupConnect(String url, int timeout) {
        return Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").ignoreHttpErrors(true);
    }

    static String createStringByPattern(String item, Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    static boolean isDoubleStr(String str) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public void init() throws Exception {
        List<String> menu = parseMenu();
        getAirfoilsByMenuList(menu);
    }

    private List<String> parseMenu() throws IOException {
        eventService.clearProgressMap();
        eventService.updateProgress("menu", 0);
        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = getJsoupConnect(HTTP_AIRFOIL_TOOLS_COM, TIMEOUT).get().body().getElementsByClass("mmenu").first();
        Elements menuList = mmenu.getElementsByTag("ul");
        Elements headerMenu = mmenu.getElementsByTag("h3");
        List<Menu> menus = new ArrayList<>();
        eventService.updateProgress("menu", 20);
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
                            eventService.addKey(prefix);
                            airfoilMenu.add(prefix);
                            menuItems.add(menuItem);
                            eventService.updateProgress("menu", eventService.getProgressValueByKey("menu") + 70 / links.size());
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
            eventService.updateProgress("menu", 100);
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

    private void getAirfoilsByMenuList(List<String> prefixList) throws InterruptedException {
        Collection<Callable<Void>> futureList = new ArrayList<>();
        for (String prefix : prefixList) {
            ParserAirfoil parserAirfoil = applicationContext.getBean(ParserAirfoil.class);
            parserAirfoil.setPrefix(prefix);
            futureList.add(parserAirfoil);
        }
        executorService.invokeAll(futureList);
    }

    public String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException {
        if (fileAirfoil.getContentType().equals("text/csv")) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileAirfoil.getInputStream()))) {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split(",");
                    if (split.length == 2 && isDoubleStr(split[0]) && isDoubleStr(split[1])) {
                        stringBuilder.append(line).append('\n');
                    } else {
                        throw new IllegalArgumentException("Невалидный файл для графика профиля");
                    }
                }
                return stringBuilder.toString();
            }
        } else {
            throw new IllegalArgumentException("Невалидный файл для графика профиля");
        }
    }
}
