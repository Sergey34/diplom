package net.sergey.diplom.service.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.service.EventService;
import net.sergey.diplom.service.properties.PropertiesHandler;
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

import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class ParserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    static int TIMEOUT = 10_000;
    private static Pattern GET_MENU_TITLE_PATTERN;
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static PropertiesHandler propertiesHandler;
    private final ApplicationContext applicationContext;
    private final DAO dao;
    private final EventService eventService;
    private final ServletContext servletContext;

    @Autowired
    public ParserService(ApplicationContext applicationContext, DAO dao, EventService eventService, ServletContext servletContext) {
        this.applicationContext = applicationContext;
        this.dao = dao;
        this.eventService = eventService;
        this.servletContext = servletContext;
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


    public void parse() throws Exception {
        String propertiesPath = servletContext.getRealPath("/WEB-INF/");
        try {
            propertiesHandler = new PropertiesHandler();
            propertiesHandler.load(propertiesPath + "/config.properties");
        } catch (IOException e) {
            LOGGER.warn("Ошибка чтения конфигурации парсера. Проверьте файл /WEB-INF/config.properties {}", Arrays.toString(e.getStackTrace()));
            throw new IllegalStateException("Ошибка чтения конфигурации парсера. Проверьте файл /WEB-INF/config.properties", e);
        }
        GET_MENU_TITLE_PATTERN = Pattern.compile(propertiesHandler.getProperty("GET_MENU_TITLE_PATTERN"));
        TIMEOUT = Integer.parseInt(propertiesHandler.getProperty("TIMEOUT"));
        List<String> menu = parseMenu();
        getAirfoilsByMenuList(menu);
    }

    private List<String> parseMenu() throws IOException {
        eventService.clearProgressMap();
        eventService.updateProgress("menu", 0.0);
        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = getJsoupConnect(propertiesHandler.getProperty("HTTP_AIRFOIL_TOOLS_COM"), TIMEOUT).get().body().getElementsByClass(propertiesHandler.getProperty("menu_class_name")).first();
        Elements menuList = mmenu.getElementsByTag(propertiesHandler.getProperty("menuList"));
        Elements headerMenu = mmenu.getElementsByTag(propertiesHandler.getProperty("headerMenu"));
        List<Menu> menus = new ArrayList<>();
        eventService.updateProgress("menu", 20.0);
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Element element = headerMenu.get(i);
            if (propertiesHandler.getProperty("menu_Header").equals(element.text())) {
                Menu menu1 = new Menu(element.text());
                List<MenuItem> menuItems = new ArrayList<>();
                Elements links = menuElement.getElementsByTag(propertiesHandler.getProperty("links"));
                for (Element link : links) {
                    Element a = link.getElementsByTag(propertiesHandler.getProperty("tegA")).first();
                    if (menu1.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                        String text = createStringByPattern(a.text(), GET_MENU_TITLE_PATTERN);
                        String prefix = createPrefix(text);
                        MenuItem menuItem = new MenuItem(text, prefix);

                        if (!prefix.equals(propertiesHandler.getProperty("filterItem"))) {
                            eventService.addKey(prefix);
                            airfoilMenu.add(prefix);
                            menuItems.add(menuItem);
                            eventService.updateProgress("menu", eventService.getProgressValueByKey("menu") + 70.0 / links.size());
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
            eventService.updateProgress("menu", 100.0);
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

    private void getAirfoilsByMenuList(List<String> prefixList) throws InterruptedException, ExecutionException {
        Collection<Callable<Void>> futureList = new ArrayList<>();
        for (String prefix : prefixList) {
            ParserAirfoil parserAirfoil = applicationContext.getBean(ParserAirfoil.class);
            parserAirfoil.setPrefix(prefix);
            futureList.add(parserAirfoil);
        }
        for (Future<Void> voidFuture : executorService.invokeAll(futureList)) {
            voidFuture.get();
        }
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

    public void stop() {
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.warn(" {}", e);
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
}
