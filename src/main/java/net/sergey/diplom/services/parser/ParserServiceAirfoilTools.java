package net.sergey.diplom.services.parser;


import net.sergey.diplom.dao.menu.DaoMenu;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.messages.MessageError;
import net.sergey.diplom.services.parser.consts.Constant;
import net.sergey.diplom.services.properties.PropertiesHandler;
import net.sergey.diplom.services.utils.UtilsLogger;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static net.sergey.diplom.dto.messages.Message.SC_NOT_IMPLEMENTED;
import static net.sergey.diplom.dto.messages.Message.SC_OK;


@Component
public class ParserServiceAirfoilTools implements ParseFileScv, Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final Constant constants;
    private final ApplicationContext applicationContext;

    private final PropertiesHandler propertiesHandler;
    private final StringHandler stringHandler;
    private final ParserMenu parseMenu;
    private final DaoMenu daoMenu;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;
    @Value("${config.parser.path}")
    private String configParserPath;
    private boolean parsingIsStarting = false;


    @Autowired
    public ParserServiceAirfoilTools(ApplicationContext applicationContext, Constant constants,
                                     PropertiesHandler propertiesHandler, StringHandler stringHandler,
                                     ParserMenu parseMenu, DaoMenu daoMenu) {
        this.applicationContext = applicationContext;
        this.constants = constants;
        this.propertiesHandler = propertiesHandler;
        this.stringHandler = stringHandler;
        this.parseMenu = parseMenu;
        this.daoMenu = daoMenu;
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
        List<Menu> menu = parseMenu.parse(getMenuItemsInDB());
        daoMenu.save(menu);
        getAirfoilsByMenuList(menu.get(0).getMenuItems());
    }

    private Collection<MenuItem> getMenuItemsInDB() {
        List<Menu> allMenu = daoMenu.findAll();
        for (Menu menu : allMenu) {
            if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                return menu.getMenuItems();
            }
        }
        return Collections.emptyList();
    }

    private void getAirfoilsByMenuList(Collection<MenuItem> menuItems) throws InterruptedException, ExecutionException {
        Collection<Callable<Void>> futureList = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            ParserAirfoil parserAirfoil = applicationContext.getBean(ParserAirfoil.class);
            parserAirfoil.setPrefix(menuItem.getUrlCode());
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
        ParserAirfoil.setFinish(true);
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
