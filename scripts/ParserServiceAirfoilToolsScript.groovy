import com.mongodb.DuplicateKeyException
import groovy.util.logging.Slf4j
import net.sergey.diplom.dao.menu.DaoMenu
import net.sergey.diplom.domain.menu.Menu
import net.sergey.diplom.domain.menu.MenuItem
import net.sergey.diplom.dto.messages.Message
import net.sergey.diplom.dto.messages.MessageError
import net.sergey.diplom.services.parser.*
import net.sergey.diplom.services.parser.consts.Constant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.core.io.Resource
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.web.multipart.MultipartFile

import java.util.concurrent.*

import static net.sergey.diplom.dto.messages.Message.SC_NOT_IMPLEMENTED
import static net.sergey.diplom.dto.messages.Message.SC_OK

@Slf4j
class ParserServiceAirfoilToolsScript implements ParseFileScv, Parser {
    private
    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private Constant constants
    @Autowired
    private ApplicationContext applicationContext
    private StringHandler stringHandler
    private ParserMenu parseMenu
    private DaoMenu daoMenu
    @Value(value = "classpath:config.properties")
    private Resource companiesXml
    @Value('${config.parser.path}')
    private String configParserPath
    private boolean parsingIsStarting = false


    @Autowired
    ParserServiceAirfoilTools(Constant constants, StringHandler stringHandler,
                              @Qualifier("parser_menu") ParserMenu parseMenu, DaoMenu daoMenu) {
        this.constants = constants
        this.stringHandler = stringHandler
        this.parseMenu = parseMenu
        this.daoMenu = daoMenu
    }

    private void parse() throws Exception {
        constants.initConst()
        List<Menu> menus = parseMenu.parse(getMenuItemsInDB())
        try {
            daoMenu.save(menus)
        } catch (DuplicateKeyException e) {
            log.trace("уже существует", e)
        }
        getAirfoilsByMenuList(menus.get(0).getItems())
    }

    private Collection<MenuItem> getMenuItemsInDB() {
        return daoMenu.findAll()?.find { menu -> menu.getHeader() == "Airfoils A to Z" }?.getItems() ?: []
    }


    private void getAirfoilsByMenuList(Collection<MenuItem> menuItems) throws InterruptedException, ExecutionException {
        Collection<Callable<Void>> futureList = new ArrayList<>()
        menuItems.each { item ->
            ParserAirfoil parserAirfoilImpl = getParserAirfoil()
            parserAirfoilImpl.setPrefix(item.getUrl())
            futureList.add(parserAirfoilImpl)
        }
        for (Future<Void> voidFuture : executorService.invokeAll(futureList)) {
            voidFuture.get()
        }
    }


    ParserAirfoil getParserAirfoil() {
        return applicationContext.getBean("parser_airfoil")
    }

    @Override
    String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException {
        if (fileAirfoil.getContentType() == "text/csv") {
            StringBuilder stringBuilder = new StringBuilder()
            new BufferedReader(new InputStreamReader(fileAirfoil.getInputStream())).eachLine { line ->
                String[] split = line.split(",")
                if (split.length == 2 && stringHandler.isDoubleStr(split[0]) && stringHandler.isDoubleStr(split[1])) {
                    stringBuilder.append(line).append('\n')
                } else {
                    throw new IllegalArgumentException("Невалидный файл для графика профиля")
                }
            }
            return stringBuilder.toString()
        } else {
            throw new IllegalArgumentException("Невалидный файл для графика профиля")
        }
    }


    @Override
    String csvToString(InputStream urlFile) {
        try {
            return urlFile.text
        } catch (ignore) {
            return null
        }
    }

    private void stop() {
        executorService.shutdownNow()
        getParserAirfoil().setFinish()
        try {
            executorService.awaitTermination(60, TimeUnit.SECONDS)
        } catch (ignored) {
            log.warn("stop Error")
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    boolean parsingIsStarting() {
        return parsingIsStarting
    }

    @Async
    Future<Message> startParsing() {
        parsingIsStarting = true
        try {
            parse()
            return new AsyncResult<>(new Message("Данные успешно загружены", SC_OK))
        } catch (Exception e) {
            log.warn("ошибка инициализации базы", e)
            e.printStackTrace()
            return new AsyncResult<>(new MessageError("Произошла ошибка при загрузке данных", SC_NOT_IMPLEMENTED, e.getStackTrace()))
        } finally {
            parsingIsStarting = false
        }
    }

    Message stopParsing() {
        if (parsingIsStarting) {
            stop()
            parsingIsStarting = false
            return new Message("done", SC_OK)
        }
        return new Message("Обновление не запущено", SC_OK)
    }

}
