package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.model.*;
import net.sergey.diplom.domain.model.messages.Message;
import net.sergey.diplom.domain.model.messages.MessageError;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.service.parser.ParserAirfoil;
import net.sergey.diplom.service.parser.ParserService;
import net.sergey.diplom.service.properties.PropertiesHandler;
import net.sergey.diplom.service.spline.AirfoilStlGenerator;
import net.sergey.diplom.service.utils.BuilderGraphs;
import net.sergey.diplom.service.utils.UtilsLogger;
import net.sergey.diplom.service.utils.imagehandlers.ImageHandler;
import net.sergey.diplom.service.utils.imagehandlers.Xy;
import net.sergey.diplom.service.utils.imagehandlers.createxychartstyle.MinimalStyle;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

import static net.sergey.diplom.domain.model.messages.Message.*;

@Service
public class ServiceImpl implements ServiceInt {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private static String PATH;
    private static String rootUrl;
    private final DAO dao;
    private final ParserService parserService;
    private final ServletContext servletContext;
    private final PropertiesHandler propertiesHandler;
    private boolean parsingIsStarting = false;

    @Autowired
    public ServiceImpl(DAO dao, ServletContext servletContext, ParserService parserService, PropertiesHandler propertiesHandler) {
        this.dao = dao;
        this.servletContext = servletContext;
        this.parserService = parserService;
        this.propertiesHandler = propertiesHandler;
    }

    public static String getRootUrl() {
        return rootUrl;
    }

    @Override
    public boolean parsingIsStarting() {
        return parsingIsStarting;
    }

    @Override
    public Message updateAirfoil(AirfoilEdit airfoilEdit) {
        if (airfoilEdit.getShortName() == null || airfoilEdit.getShortName().isEmpty()) {
            return new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        try {
            dao.addAirfoil(airfoil);
        } catch (Exception e) {
            LOGGER.warn("Ошибка при обновлении airfoil {}", e);
            return new Message("Ошибка при добавлении в базу нового airfoil", SC_CONFLICT);
        }
        LOGGER.debug("Airfoil успешно обновлен {}", SC_OK);
        return new Message("Airfoil успешно обновлен", SC_OK);
    }

    private void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (dao.getMenuItemByUrl(String.valueOf(airfoil.getPrefix().getPrefix())) == null) {
            List<Menu> allMenu = dao.getAllMenu();
            for (Menu menu : allMenu) {
                ;
                if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                    MenuItem menuItem = MenuItem.createMenuItemByNewPrefix(airfoil.getPrefix());
                    menu.getMenuItems().add(menuItem);
                    break;
                }
            }
            dao.addMenus(allMenu);
        }
    }

    private Airfoil getAirfoilByAirfoilEdit(AirfoilEdit airfoilEdit) {
        Airfoil airfoil = new Airfoil();
        airfoil.setName(airfoilEdit.getAirfoilName());
        airfoil.setShortName(airfoilEdit.getShortName());
        airfoil.setCoordView(airfoilEdit.getViewCsv());
        airfoil.setDescription(airfoilEdit.getDetails());
        airfoil.setPrefix(new Prefix(airfoilEdit.getShortName().toUpperCase().charAt(0)));
        Set<Coordinates> coordinates = new HashSet<>();
        for (Data data : airfoilEdit.getData()) {
            Coordinates coordinateItem = new Coordinates(data.getData(), data.getFileName());
            coordinateItem.setRenolgs(data.getReynolds());
            coordinateItem.setNCrit(data.getnCrit());
            coordinateItem.setMaxClCd(data.getMaxClCd());
            coordinates.add(coordinateItem);
            coordinates.add(coordinateItem);
        }
        airfoil.setCoordinates(coordinates);
        return airfoil;
    }

    @Override
    public List<Menu> getMenu() {
        List<Menu> allMenu = dao.getAllMenu();
        for (Menu menu : allMenu) {
            Collections.sort(menu.getMenuItems(), new Comparator<MenuItem>() {
                @Override
                public int compare(MenuItem o1, MenuItem o2) {
                    return o1.getUrlCode().charAt(0) - o2.getUrlCode().charAt(0);
                }
            });
        }
        return allMenu;
    }

    @Override
    public Message addUser(UserView userView) {
        User user = new User();
        user.setEnabled(1);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        user.setUserRoles(findUserRoleByName(userView.getRole()));
        try {
            dao.addUser(user);
            LOGGER.trace("Пользователь успешно создан");
            return new Message("Пользователь успешно создан", SC_OK);
        } catch (ConstraintViolationException e) {
            LOGGER.trace("пользователь с именем {} уже существует в базе.", user.getUserName());
            return new Message("Пользователь с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        } catch (Exception e) {
            LOGGER.trace("Ошибка при добавлении пользователя {}, {}", user.getUserName(), e);
            return new Message("Ошибка при добавлении пользователя", SC_CONFLICT);
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return dao.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        PATH = servletContext.getRealPath("/resources/");
        rootUrl = servletContext.getContextPath();
        try {
            propertiesHandler.load(servletContext.getRealPath("/WEB-INF/") + "/config.properties");
        } catch (IOException e) {
            LOGGER.warn("Ошибка при попытке инициализировать настройки парсера");
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(servletContext.getRealPath("/static/js/getContextPath.js")))) {
            bufferedWriter.write("let rootUrl = '" + rootUrl + "';");
        } catch (IOException e) {
            LOGGER.warn("Ошибка при инициализации rootUrl {}", e);
        }
    }


    private Set<UserRole> findUserRoleByName(List<String> roleNames) {
        Map<String, UserRole> userRoleMap = initUserRoleMap();
        Set<UserRole> userRoles = new HashSet<>();
        for (String roleName : roleNames) {
            userRoles.add(userRoleMap.get(roleName));
        }
        return userRoles;
    }

    private Map<String, UserRole> initUserRoleMap() {
        Map<String, UserRole> userRoleMap = new HashMap<>();
        for (UserRole userRole : dao.getAllUserRoles()) {
            userRoleMap.put(userRole.getRole(), userRole);
        }
        return userRoleMap;
    }

    @Override
    public List<AirfoilDTO> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = dao.getAirfoilsByPrefix(prefix, startNumber, count);
        for (Airfoil airfoil : airfoilsByPrefix) {
            drawViewAirfoil(airfoil);
        }
        return AirfoilDTO.mapAirfoilOnAirfoilId(airfoilsByPrefix);
    }

    private void fillListXListY(List<Double> x, List<Double> y, String[] split) {
        for (String line : split) {
            try {
                String[] strings = line.trim().split(",");
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                LOGGER.warn("Оштбка чтения файла {}", e);
                break;
            }
        }
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count) {
        return new ArrayList<>();
    }

    @Override
    public List<String> updateGraf(String airfoilId, List<String> checkedList) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (airfoil != null) {
            try {
                new BuilderGraphs(PATH).draw(airfoil, checkedList, true);
            } catch (Exception e) {
                LOGGER.warn("Ошибка при обработке файловк с координатами {}", e);
            }
        }
        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : CHART_NAMES) {
            imgCsvName.add("/resources/chartTemp/" + airfoilId + chartName + ".png");
        }
        return imgCsvName;
    }

    @Override
    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (null == airfoil) {
            return null;
        }
        String stlFilePath = null;
        try {
            new BuilderGraphs(PATH).draw(airfoil, null, false);
            stlFilePath = new AirfoilStlGenerator().generate(airfoil.getShortName(), airfoil.getCoordView(), PATH);
        } catch (Exception e) {
            LOGGER.warn("Ошибка при обработке файлов с координатами {}", e);
        }
        drawViewAirfoil(airfoil);
        return new AirfoilDetail(airfoil, CHART_NAMES, stlFilePath);
    }

    private void drawViewAirfoil(Airfoil airfoil) {
        if (new File(PATH + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
            return;
        }
        if (airfoil.getCoordView() == null || airfoil.getCoordView().isEmpty()) {
            LOGGER.warn("отсутствубт координаты для airfoil {}", airfoil.getShortName());
            return;
        }
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        fillListXListY(x, y, airfoil.getCoordView().split("\n"));
        ImageHandler.setSavePath(PATH, "/airfoil_img/");
        ImageHandler imageHandler = new ImageHandler(airfoil.getShortName(), new Xy(x, y, " "), new MinimalStyle());
        try {
            imageHandler.draw();
        } catch (Exception e) {
            LOGGER.warn("Ошибка при рисовании графиков {}", e);
        }

    }


    @Override
    @Async("executor")
    public Future<Message> parse() {
        parsingIsStarting = true;
        try {
            parserService.parse();
            return new AsyncResult<>(new Message("Данные успешно загружены", SC_OK));
        } catch (Exception e) {
            LOGGER.warn("ошибка инициализации базы {}", e);
            return new AsyncResult<Message>(new MessageError("Произошла ошибка при загрузке данных", SC_NOT_IMPLEMENTED, e.getStackTrace()));
        } finally {
            parsingIsStarting = false;
        }
    }


    @Override
    public String getCurrentUserInfo() {
        Boolean isLogin = UtilsLogger.getAuthentication().isAuthenticated();
        if (!"guest".equals(UtilsLogger.getAuthentication().getName()) && isLogin) {
            return UtilsLogger.getAuthentication().getName();
        }
        return null;
    }

    @Override
    public Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        if (name.isEmpty()) {
            LOGGER.debug("Имя не должно быть пустым");
            return new Message("Имя не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        Airfoil airfoil = new Airfoil(name, details, shortName);
        if (dao.getAirfoilById(shortName) != null) {
            LOGGER.debug("Airfoil с таким именем уже существует, Выберите другое имя");
            return new Message("Airfoil с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        }
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    private Message addUpdateAirfoil(MultipartFile fileAirfoil, List<MultipartFile> files, Airfoil airfoil) {
        try {
            if (fileAirfoil == null || fileAirfoil.isEmpty()) {
                airfoil.setCoordView(dao.getAirfoilById(airfoil.getShortName()).getCoordView());
            } else {
                airfoil.setCoordView(parserService.parseFileAirfoil(fileAirfoil));
            }
            if (files == null || files.size() == 1 && files.get(0).isEmpty()) {
                airfoil.setCoordinates(dao.getAirfoilById(airfoil.getShortName()).getCoordinates());
            } else {
                airfoil.setCoordinates(createCoordinateSet(files));
            }
            addMenuItemForNewAirfoil(airfoil);
            dao.addAirfoil(airfoil);
        } catch (Exception e) {
            LOGGER.warn("Один из файлов имеет не верный формат {}", e);
            return new Message("Один из файлов имеет не верный формат", SC_NOT_ACCEPTABLE);
        }
        LOGGER.debug("Airfoil успешно добален / обновлен");
        return new Message("Airfoil успешно добален / обновлен", SC_OK);

    }


    @Override
    public Message updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        if (name.isEmpty()) {
            return new Message("Имя не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        Airfoil airfoil = new Airfoil(name, details, shortName);
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    @Override
    public Message addAirfoil(AirfoilEdit airfoilEdit) {
        if (airfoilEdit.getShortName() == null || airfoilEdit.getShortName().isEmpty()) {
            LOGGER.debug("airfoil не добавлен - Короткое имя профиля не должно быть пустым");
            return new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        if (dao.getAirfoilById(airfoilEdit.getShortName()) != null) {
            LOGGER.debug("Airfoil с таким именем уже существует, Выберите другое имя");
            return new Message("Airfoil с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        try {
            dao.addAirfoil(airfoil);
        } catch (Exception e) {
            LOGGER.warn("ошибка при добавлении в базу нового airfoil {}", e);
            return new Message("Ошибка при добавлении в базу нового airfoil", SC_CONFLICT);
        }
        return new Message("Airfoil успешно добавлен", SC_OK);
    }

    @Override
    public Message stop() {
        parserService.stop();
        parsingIsStarting = false;
        return null;
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        return dao.getCountAirfoilByPrefix(prefix);
    }

    private Set<Coordinates> createCoordinateSet(List<MultipartFile> files) throws IOException {
        Set<Coordinates> coordinates = new HashSet<>();
        for (MultipartFile file : files) {
            coordinates.add(new Coordinates(ParserAirfoil.csvToString(file.getInputStream()), file.getOriginalFilename()));
        }
        return coordinates;
    }
}
