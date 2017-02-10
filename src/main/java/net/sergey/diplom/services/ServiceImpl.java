package net.sergey.diplom.services;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.UserDto;
import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.airfoil.Data;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.messages.MessageError;
import net.sergey.diplom.services.parser.ParserAirfoil;
import net.sergey.diplom.services.parser.ParserService;
import net.sergey.diplom.services.properties.PropertiesHandler;
import net.sergey.diplom.services.spline.AirfoilStlGenerator;
import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import net.sergey.diplom.services.utils.BuilderGraphs;
import net.sergey.diplom.services.utils.UtilsLogger;
import net.sergey.diplom.services.utils.imagehandlers.ImageHandler;
import net.sergey.diplom.services.utils.imagehandlers.Xy;
import net.sergey.diplom.services.utils.imagehandlers.createxychartstyle.MinimalStyle;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Future;

import static net.sergey.diplom.dto.messages.Message.*;

@Service
public class ServiceImpl implements ServiceInt {
    protected static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final DAO dao;
    private final ParserService parserService;
    private final PropertiesHandler propertiesHandler;
    private final Converter converter;
    private final FileSystemStorageService storageService;
    private boolean parsingIsStarting = false;
    @Value("${config.parser.path}")
    private String configParserPath;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;

    @Autowired
    public ServiceImpl(DAO dao, ParserService parserService, PropertiesHandler propertiesHandler, Converter converter, FileSystemStorageService storageService) {
        this.dao = dao;
        this.parserService = parserService;
        this.propertiesHandler = propertiesHandler;
        this.converter = converter;
        this.storageService = storageService;
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

    @Override
    public List<AirfoilDTO> getAllAirfoilDto(int startNumber, int count) {
        List<Airfoil> allAirfoils = dao.getAllAirfoils(startNumber, count, true);
        for (Airfoil airfoil : allAirfoils) {
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(allAirfoils);
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = dao.getAirfoilsByPrefix(prefix, startNumber, count, false);
        for (Airfoil airfoil : airfoilsByPrefix) {
            createDatFile(airfoil);
        }
        return airfoilsByPrefix;
    }

    @Override
    public Airfoil getAirfoilById(String airfoilId) {
        Airfoil airfoilById = dao.getAirfoilById(airfoilId);
        createDatFile(airfoilById);
        return airfoilById;
    }

    private void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (dao.getMenuItemByUrl(String.valueOf(airfoil.getPrefix().getPrefix())) == null) {
            List<Menu> allMenu = dao.getAllMenu();
            for (Menu menu : allMenu) {
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
        user.setEnabled(true);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
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
    public List<Authorities> getAllUserRoles() {
        return dao.getAllUserRoles();
    }

    @PostConstruct
    public void init() {
        try {
            if (!new File(configParserPath).exists()) {
                propertiesHandler.load(companiesXml.getInputStream());
            } else {
                propertiesHandler.load(configParserPath);
            }
        } catch (IOException e) {
            LOGGER.warn("Ошибка при попытке инициализировать настройки парсера");
        }

        if (dao.getUserByName("admin") == null || dao.getRoleByUsername("admin").size() == 0) {
            User user = new User();
            user.setEnabled(true);
            user.setUserName("admin");
            String password = new BCryptPasswordEncoder().encode("mex_mat");
            user.setPassword(password);
            Authorities adminRole = new Authorities("ROLE_ADMIN", "admin");
            dao.addUser(user);
            dao.addAuthority(adminRole);
        }
    }

    @Override
    public List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = dao.getAirfoilsByPrefix(prefix, startNumber, count, true);
        for (Airfoil airfoil : airfoilsByPrefix) {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(airfoilsByPrefix);
    }

    private void createDatFile(Airfoil airfoil) {
        String s = storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".dat";
        File file = new File(s);
        if (!file.exists()) {
            try {
                file.createNewFile();
                Files.write(file.toPath(), airfoil.getCoordView().getBytes());
            } catch (IOException e) {
                LOGGER.warn("Ошибка записи файла", e);
            }
        }
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
        List<Airfoil> allAirfoils = dao.getAllAirfoils(startNumber, count, false);
        for (Airfoil airfoil : allAirfoils) {
            createDatFile(airfoil);
        }
        return allAirfoils;
    }

    @Override
    public List<String> updateGraf(String airfoilId, List<String> checkedList) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (airfoil != null) {
            try {
                new BuilderGraphs(storageService).draw(airfoil, checkedList, true);
            } catch (Exception e) {
                LOGGER.warn("Ошибка при обработке файловк с координатами {}", e);
            }
        }
        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : CHART_NAMES) {
            imgCsvName.add("/files/chartTemp/" + airfoilId + chartName + ".png");
        }
        return imgCsvName;
    }

    @Override
    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = dao.getAirfoilById(airfoilId);
        if (null == airfoil) {
            return null;
        }
        try {
            new BuilderGraphs(storageService).draw(airfoil, null, false);
            new AirfoilStlGenerator().generate(airfoil.getShortName(), airfoil.getCoordView(), storageService);
        } catch (Exception e) {
            LOGGER.warn("Ошибка при обработке файлов с координатами {}", e);
        }
        drawViewAirfoil(airfoil);
        return converter.airfoilToAirfoilDetail(airfoil);
    }

    private void drawViewAirfoil(Airfoil airfoil) {
        if (new File(storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
            return;
        }
        if (airfoil.getCoordView() == null || airfoil.getCoordView().isEmpty()) {
            LOGGER.warn("отсутствубт координаты для airfoil {}", airfoil.getShortName());
            return;
        }
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        fillListXListY(x, y, airfoil.getCoordView().split("\n"));
        ImageHandler.setSavePath(storageService.getRootLocation() + "/airfoil_img/");
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
            e.printStackTrace();
            return new AsyncResult<Message>(new MessageError("Произошла ошибка при загрузке данных", SC_NOT_IMPLEMENTED, e.getStackTrace()));
        } finally {
            parsingIsStarting = false;
        }
    }


    @Override
    public UserDto getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isLogin = authentication.isAuthenticated();
        String name = authentication.getName();
        if (!"anonymousUser".equals(name) && isLogin) {
            return converter.userToUserDto(dao.getUserByName(name), dao.getRoleByUsername(name));
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
        if (parsingIsStarting) {
            parserService.stop();
            parsingIsStarting = false;
            return new Message("done", SC_OK);
        }
        return new Message("Обновление не запущено", SC_OK);
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
