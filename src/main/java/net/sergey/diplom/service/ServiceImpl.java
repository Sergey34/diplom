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
import net.sergey.diplom.service.parser.Parser;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ServiceImpl implements ServiceInt {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private static String PATH;
    private final DAO dao;
    private final Parser parser;
    private final ServletContext servletContext;

    @Autowired
    public ServiceImpl(DAO dao, ServletContext servletContext, Parser parser) {
        this.dao = dao;
        this.servletContext = servletContext;
        this.parser = parser;
    }

    @Override
    public List<Menu> getMenu() throws IOException {
        List<Menu> allMenu = dao.getAllMenu();
        for (Menu menu : allMenu) {
            menu.getMenuItems().sort(new Comparator<MenuItem>() {
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
    public AirfoilDetail getDetailInfo(int airfoilId) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (null == airfoil) {
            return AirfoilDetail.getAirfoilDetailError("airfoil не найден");
        }
        if (!filesExist(airfoil)) {
            try {
                new BuilderFiles(PATH).draw(airfoil);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn("Ошибка при обработке файловк с координатами {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                return new AirfoilDetail(airfoil, e.getMessage());
            }
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
    public void parse() throws IOException {
        parser.setPath(PATH).init();
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
            airfoil.setCoordView(parseFileAirfoil(fileAirfoil));
            airfoil.setCoordinates(parseCoordinates(files));
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

    private Set<Coordinates> parseCoordinates(List<MultipartFile> files) throws IOException {
        Set<Coordinates> coordinates = new HashSet<>();
        for (MultipartFile file : files) {
            coordinates.add(new Coordinates(parser.csvToString(file.getInputStream()), file.getOriginalFilename()));
        }
        return coordinates;
    }

    private String parseFileAirfoil(MultipartFile fileAirfoil) throws IOException {
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

    private boolean filesExist(Airfoil airfoil) {
        for (Coordinates coordinates : airfoil.getCoordinates()) {
            File file = new File(PATH + "/tmpCsv/" + coordinates.getFileName() + ".csv");
            if (!file.exists()) {
                return false;
            }
        }
        for (String chartName : CHART_NAMES) {
            File file = new File(PATH + "/chartTemp/" + airfoil.getId() + chartName + ".bmp");
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    private boolean isDoubleStr(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
