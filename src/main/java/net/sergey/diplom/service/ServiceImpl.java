package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilDTO;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.model.UserView;
import net.sergey.diplom.service.parser.ParserService;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import net.sergey.diplom.service.utils.imagehandlers.ImageHandler;
import net.sergey.diplom.service.utils.imagehandlers.Xy;
import net.sergey.diplom.service.utils.imagehandlers.createxychartstyle.MinimalStyle;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ServiceImpl implements ServiceInt {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private static String PATH;
    private final DAO dao;
    private final ParserService parserService;
    private final ServletContext servletContext;

    @Autowired
    public ServiceImpl(DAO dao, ServletContext servletContext, ParserService parserService) {
        this.dao = dao;
        this.servletContext = servletContext;
        this.parserService = parserService;
    }

    @Override
    public List<Menu> getMenu() throws IOException {
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
    public List<User> getUser(String name) {
        return dao.getUserByName(name);
    }

    @Override
    public boolean isValidUser(String name) {
        return getUser(name).size() == 0;
    }

    @Override
    public boolean addUser(UserView userView) {
        User user = new User();
        user.setEnabled(1);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        user.setUserRoles(UtilRoles.findUserRoleByName(userView.getRole()));
        try {
            dao.addUser(user);
            return true;
        } catch (ConstraintViolationException e) {
            LOGGER.warn("пользователь с именем {} уже существует в базе. {}", user.getUserName(), e.getStackTrace());
            return false;
        }
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return dao.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        UtilRoles.init(dao.getAllUserRoles());
        PATH = servletContext.getRealPath("/resources/");
    }

    @Override
    public void clean() {
        dao.cleanAllTables();
    }

    @Override
    public List<AirfoilDTO> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = dao.getAirfoilsByPrefix(prefix, startNumber, count);
        for (Airfoil airfoil : airfoilsByPrefix) {
            drawViewAirfoil(airfoil);
        }
        return Mapper.mapAirfoilOnAirfoilId(airfoilsByPrefix);
    }

    private void fillListXListY(List<Double> x, List<Double> y, String[] split) {
        for (String line : split) {
            try {
                String[] strings = line.trim().split(",");
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                LOGGER.warn("Оштбка чтения файла");
                e.printStackTrace();
                throw e;
            }
        }
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count) {
        return new ArrayList<>();
    }

    @Override
    public List<String> updateGraf(int airfoilId, List<String> checkeds) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (airfoil != null) {
            try {
                new BuilderFiles(PATH).draw(airfoil, checkeds);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn("Ошибка при обработке файловк с координатами {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            }
        }
        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : CHART_NAMES) {
            imgCsvName.add("/resources/chartTemp/" + airfoilId + chartName + ".png");
        }
        return imgCsvName;

    }

    @Override
    public AirfoilDetail getDetailInfo(int airfoilId) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (null == airfoil) {
            return AirfoilDetail.getAirfoilDetailError("airfoil не найден");
        }
        try {
            new BuilderFiles(PATH).draw(airfoil, null);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("Ошибка при обработке файловк с координатами {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            return new AirfoilDetail(airfoil, e.getMessage());
        }
        drawViewAirfoil(airfoil);
        return new AirfoilDetail(airfoil, CHART_NAMES);
    }

    @Async
    private void drawViewAirfoil(Airfoil airfoil) {
        if (new File(PATH + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
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
            e.printStackTrace();
        }
    }

    @Override
    public void parse() {
        try {
            parserService.init();
        } catch (Exception e) {
            LOGGER.warn("ошибка инициализации базы {}", Arrays.asList(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    @Override
    public String getUserInfo() {
        Boolean isLogin = UtilsLogger.getAuthentication().isAuthenticated();
        if (!"guest".equals(UtilsLogger.getAuthentication().getName()) && isLogin) {
            return UtilsLogger.getAuthentication().getName();
        }
        return null;
    }

    @Override
    public boolean createNewAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        Airfoil airfoil = new Airfoil(name, details, shortName);
        try {
            airfoil.setCoordView(parserService.parseFileAirfoil(fileAirfoil));
            airfoil.setCoordinates(createCoordinateSet(files));
            dao.addAirfoil(airfoil);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        return dao.getCountAirfoilByPrefix(prefix);
    }


    private Set<Coordinates> createCoordinateSet(List<MultipartFile> files) throws IOException {
        Set<Coordinates> coordinates = new HashSet<>();
        for (MultipartFile file : files) {
            coordinates.add(new Coordinates(parserService.csvToString(file.getInputStream()), file.getOriginalFilename()));
        }
        return coordinates;
    }
}
