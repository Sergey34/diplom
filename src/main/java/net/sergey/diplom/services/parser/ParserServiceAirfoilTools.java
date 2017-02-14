package net.sergey.diplom.services.parser;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.messages.MessageError;
import net.sergey.diplom.services.EventService;
import net.sergey.diplom.services.parser.consts.Constant;
import net.sergey.diplom.services.parser.siteconnection.ConnectionManager;
import net.sergey.diplom.services.properties.PropertiesHandler;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static net.sergey.diplom.dto.messages.Message.SC_NOT_IMPLEMENTED;
import static net.sergey.diplom.dto.messages.Message.SC_OK;


@Component
public class ParserServiceAirfoilTools implements ParseFileScv, Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final Constant constants;
    private final ApplicationContext applicationContext;
    private final DAO dao;
    private final EventService eventService;
    private final PropertiesHandler propertiesHandler;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;
    @Value("${config.parser.path}")
    private String configParserPath;
    private boolean parsingIsStarting = false;
    private final ConnectionManager connectionManager;
    private final StringHandler stringHandler;

    @Autowired
    public ParserServiceAirfoilTools(ApplicationContext applicationContext, DAO dao, EventService eventService,
                                     Constant constants, PropertiesHandler propertiesHandler,
                                     ConnectionManager connectionManager, StringHandler stringHandler) {
        this.applicationContext = applicationContext;
        this.dao = dao;
        this.eventService = eventService;
        this.constants = constants;
        this.propertiesHandler = propertiesHandler;
        this.connectionManager = connectionManager;
        this.stringHandler = stringHandler;
    }

    private void parse() throws Exception {
        try {
            if (!new File(configParserPath).exists()) {
                propertiesHandler.load(companiesXml.getInputStream());
            } else {
                propertiesHandler.load(configParserPath);
            }
        } catch (IOException e) {
            LOGGER.warn("Ошибка чтения конфигурации парсера. Проверьте файл /WEB-INF/config.properties", e);
            throw new IllegalStateException("Ошибка чтения конфигурации парсера. Проверьте файл /WEB-INF/config.properties", e);
        }
        constants.initConst();
        List<String> menu = parseMenu();
        getAirfoilsByMenuList(menu);
    }

    private List<String> parseMenu() throws IOException {
        eventService.clearProgressMap();
        eventService.updateProgress("menu", 0.0);
        final List<String> airfoilMenu = new ArrayList<>();
        Element mmenu = connectionManager.getJsoupConnect(constants.HTTP_AIRFOIL_TOOLS_COM, constants.TIMEOUT).get().body().getElementsByClass(constants.MENU_CLASS_NAME).first();
        Elements menuList = mmenu.getElementsByTag(constants.MENU_LIST);
        Elements headerMenu = mmenu.getElementsByTag(constants.HEADER_MENU);
        List<Menu> menus = new ArrayList<>();
        eventService.updateProgress("menu", 20.0);
        for (int i = 0; i < menuList.size(); i++) {
            Element menuElement = menuList.get(i);
            Element element = headerMenu.get(i);
            if (constants.MENU_HEADER.equals(element.text())) {
                Menu menu1 = new Menu(element.text());
                Set<MenuItem> menuItems = new HashSet<>();
                menuItems.addAll(getMenuItemsInDB());
                Elements links = menuElement.getElementsByTag(constants.LINKS);
                for (Element link : links) {
                    Element a = link.getElementsByTag(constants.TEGA).first();
                    if (menu1.getHeader().equals(constants.MENU_HEADER)) {
                        String text = stringHandler.createStringByPattern(a.text().trim(), constants.GET_MENU_TITLE_PATTERN);
                        String prefix = createPrefix(text);
                        MenuItem menuItem = new MenuItem(text, prefix);

                        if (!prefix.equals(constants.FILTER_ITEM)) {
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
            LOGGER.warn("Элемент меню: {} \n уже существует в базе ", menus, e);
            throw e;
        }
        return airfoilMenu;
    }

    private Collection<MenuItem> getMenuItemsInDB() {
        List<Menu> allMenu = dao.getAllMenu();
        for (Menu menu : allMenu) {
            if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                return menu.getMenuItems();
            }
        }
        return Collections.emptyList();
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

    @Override
    public String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException {
        if (fileAirfoil.getContentType().equals("text/csv")) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileAirfoil.getInputStream()))) {
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split(",");
                    if (split.length == 2 && stringHandler.isDoubleStr(split[0]) && stringHandler.isDoubleStr(split[1])) {
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

    @Override
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

    private void stop() {
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("stop Error", e);
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public boolean parsingIsStarting() {
        return parsingIsStarting;
    }

    @Async("executor")
    public Future<Message> startParsing() {
        parsingIsStarting = true;
        try {
            parse();
            return new AsyncResult<>(new Message("Данные успешно загружены", SC_OK));
        } catch (Exception e) {
            LOGGER.warn("ошибка инициализации базы", e);
            e.printStackTrace();
            return new AsyncResult<Message>(new MessageError("Произошла ошибка при загрузке данных", SC_NOT_IMPLEMENTED, e.getStackTrace()));
        } finally {
            parsingIsStarting = false;
        }
    }

    public Message stopParsing() {
        if (parsingIsStarting) {
            stop();
            parsingIsStarting = false;
            return new Message("done", SC_OK);
        }
        return new Message("Обновление не запущено", SC_OK);
    }

}
