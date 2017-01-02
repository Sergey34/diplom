package parser.service;


import base.domain.menu.Menu;
import base.domain.menu.MenuItem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import parser.dao.MenuDao;
import parser.service.constants.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
public class ParserService implements ParserServiceInt {
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ApplicationContext applicationContext;
    private final MenuDao menuDao;
    private final Constant constants;


    @Autowired
    public ParserService(@NonNull ApplicationContext applicationContext,
                         @NonNull Constant constants, @NonNull MenuDao menuDao) {
        this.applicationContext = applicationContext;
        this.constants = constants;
        this.menuDao = menuDao;
    }

    static Connection getJsoupConnect(@NonNull String url, int timeout) {
        return Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").ignoreHttpErrors(true);
    }

    static String createStringByPattern(@NonNull String item, @NonNull Pattern pattern) {
        Matcher matcher = pattern.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    static boolean isDoubleStr(@NonNull String str) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    public String parse() {
        try {
            List<String> menu = parseMenu();
            getAirfoilsByMenuList(menu);
        } catch (Exception e) {
            log.warn("Ошибка", e);
            return "{NOT_OK }" + e.toString();
        }
        return "{OK}";
    }

    private List<String> parseMenu() throws IOException {
        // eventService.clearProgressMap();
        //  eventService.updateProgress("menu", 0.0);
        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = getJsoupConnect(constants.HTTP_AIRFOIL_TOOLS_COM, constants.TIMEOUT).get().body().getElementsByClass(constants.MENU_CLASS_NAME).first();
        Elements menuList = mmenu.getElementsByTag(constants.MENU_LIST);
        Elements headerMenu = mmenu.getElementsByTag(constants.HEADER_MENU);
        List<Menu> menus = new ArrayList<>();
        //  eventService.updateProgress("menu", 20.0);
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Element element = headerMenu.get(i);
            if (constants.MENU_HEADER.equals(element.text())) {
                Menu menu1 = new Menu(element.text());
                List<MenuItem> menuItems = new ArrayList<>();
                Elements links = menuElement.getElementsByTag(constants.LINKS);
                for (Element link : links) {
                    Element a = link.getElementsByTag(constants.TEGA).first();
                    if (menu1.getHeader().equals(constants.MENU_HEADER)) {
                        String text = createStringByPattern(a.text(), constants.GET_MENU_TITLE_PATTERN);
                        String prefix = createPrefix(text);
                        MenuItem menuItem = new MenuItem(text, prefix);

                        if (!prefix.equals(constants.FILTER_ITEM)) {
                            //   eventService.addKey(prefix);
                            airfoilMenu.add(prefix);
                            menuItem.setMenu(menu1);
                            menuItems.add(menuItem);
                            //  eventService.updateProgress("menu", eventService.getProgressValueByKey("menu") + 70.0 / links.size());
                        }
                    }
                }
                menu1.setMenuItems(menuItems);
                menus.add(menu1);
                break;
            }
        }

        try {
            menuDao.save(menus);
//            eventService.updateProgress("menu", 100.0);
        } catch (ConstraintViolationException e) {
            log.warn("Элемент меню: {} \n уже существует в базе", menus, e);
            throw e;
        }
        return airfoilMenu;
    }

    private String createPrefix(@NonNull String text) {
        if (!"".equals(text)) {
            return String.valueOf(text.charAt(0)).toLowerCase();
        } else {
            return "allAirfoil";
        }
    }

    private void getAirfoilsByMenuList(@NonNull List<String> prefixList) throws InterruptedException, ExecutionException {
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

    public String parseFileAirfoil(@NonNull MultipartFile fileAirfoil) throws IOException {
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
            log.warn("Ошибка", e);
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public String parsePrefix(@NonNull String prefix) {
        ParserAirfoil parserAirfoil = applicationContext.getBean(ParserAirfoil.class);
        parserAirfoil.setPrefix(prefix);
        try {
            executorService.submit(parserAirfoil).get();
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Ошибка при парсинге префикса {}", prefix, e);
            return "Not_OK";
        }
        return "OK";
    }

    public String parseAirfoil(@NonNull String airfoilId) {
        ParserAirfoil parserAirfoil = applicationContext.getBean(ParserAirfoil.class);
        try {
            parserAirfoil.addAirfoilById(airfoilId);
        } catch (Exception e) {
            log.warn("Ошибка при парсинге airfoilId {}", airfoilId, e);
            return "NOT_OK";
        }
        return "OK";
    }
}
