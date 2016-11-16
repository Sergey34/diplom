package net.sergey.diplom.service;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilAbstract;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.model.UserView;
import net.sergey.diplom.service.parser.Parser;
import net.sergey.diplom.service.utils.UtilRoles;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ServiceImpl implements ServiceInt {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static final List<String> CHART_NAMES = Arrays.asList("Cl v Cd", "Cl v Alpha", "Alpha v Cd", "Alpha v Cm", "Cl|Cd v Alpha");
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
        return dao.getAllMenu();
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
    public List<AirfoilAbstract> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        return Mapper.mapAirfoilOnAirfoilId(dao.getAirfoilsByPrefix(prefix, startNumber, count));

    }

    @Override
    public boolean updateAirfoil(AirfoilView airfoilView) {
        Airfoil airfoil = new Airfoil(airfoilView.getName(),
                airfoilView.getDescription(),
                airfoilView.getImage(),
                airfoilView.getId(),
                new Prefix(airfoilView.getPrefix().charAt(0)));
        dao.addAirfoil(airfoil);
        return false;
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
        return new AirfoilDetail(airfoil, CHART_NAMES);
    }

    @Override
    public String fileUpload(String name, MultipartFile file) {
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(
                    new File(PATH + "/airfoil_img/" + name + "-uploaded")));
            stream.write(file.getBytes());
            stream.close();
            return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
        } catch (IOException e) {
            LOGGER.warn("Ошибка при загрузке файла {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            return "Вам не удалось загрузить " + name + " => " + e.getMessage();
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
}
