package net.sergey.diplom.services.mainservice;


import net.sergey.diplom.dao.Filter;
import net.sergey.diplom.dao.airfoil.DaoAirfoil;
import net.sergey.diplom.dao.airfoil.DaoCoordinates;
import net.sergey.diplom.dao.menu.DaoMenu;
import net.sergey.diplom.dao.menu.DaoMenuItem;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.airfoil.Data;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.services.buildergraphs.BuilderGraphs;
import net.sergey.diplom.services.buildergraphs.imagehandlers.ImageHandler;
import net.sergey.diplom.services.buildergraphs.imagehandlers.Xy;
import net.sergey.diplom.services.buildergraphs.imagehandlers.createxychartstyle.MinimalStyle;
import net.sergey.diplom.services.parser.ParseFileScv;
import net.sergey.diplom.services.properties.PropertiesHandler;
import net.sergey.diplom.services.stlgenerators.AirfoilStlGenerator;
import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static net.sergey.diplom.dto.messages.Message.*;

@Service
public class ServiceAirfoilTools implements ServiceAirfoil {
    private static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final ParseFileScv parseFileScv;
    private final PropertiesHandler propertiesHandler;
    private final Converter converter;
    private final FileSystemStorageService storageService;
    private final AirfoilStlGenerator stlGenerator;
    private final DaoMenu daoMenu;
    private final DaoMenuItem daoMenuItem;
    private final DaoAirfoil daoAirfoil;
    private final DaoCoordinates daoCoordinates;
    @Value("${config.parser.path}")
    private String configParserPath;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;

    @Autowired
    public ServiceAirfoilTools(ParseFileScv parseFileScv, PropertiesHandler propertiesHandler,
                               Converter converter, FileSystemStorageService storageService,
                               AirfoilStlGenerator stlGenerator, DaoMenu daoMenu,
                               DaoMenuItem daoMenuItem, DaoAirfoil daoAirfoil, DaoCoordinates daoCoordinates) {
        this.parseFileScv = parseFileScv;
        this.propertiesHandler = propertiesHandler;
        this.converter = converter;
        this.storageService = storageService;
        this.stlGenerator = stlGenerator;
        this.daoMenu = daoMenu;
        this.daoMenuItem = daoMenuItem;
        this.daoAirfoil = daoAirfoil;
        this.daoCoordinates = daoCoordinates;
    }

    @Override
    public Message updateAirfoil(AirfoilEdit airfoilEdit) {
        if (airfoilEdit.getShortName() == null || airfoilEdit.getShortName().isEmpty()) {
            return new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        try {
            storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            LOGGER.warn("Ошибка при обновлении airfoil ", e);
            return new Message("Ошибка при добавлении в базу нового airfoil", SC_CONFLICT);
        }
        LOGGER.debug("Airfoil успешно обновлен {}", SC_OK);
        return new Message("Airfoil успешно обновлен", SC_OK);
    }

    @Override
    public List<AirfoilDTO> getAllAirfoilDto(int startNumber, int count) {
        List<Airfoil> allAirfoils = daoAirfoil.findAll(new PageRequest(startNumber, count));
        for (Airfoil airfoil : allAirfoils) {
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(allAirfoils);
    }

    @Override
    public List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = daoAirfoil.findByPrefixOrderByShortName(new Prefix(prefix), new PageRequest(startNumber, count));
        for (Airfoil airfoil : airfoilsByPrefix) {
            createDatFile(airfoil);
        }
        return airfoilsByPrefix;
    }

    @Override
    public Airfoil getAirfoilById(String airfoilId) {
        Airfoil airfoilById = daoAirfoil.findOneByShortName(airfoilId);
        createDatFile(airfoilById);
        return airfoilById;
    }

    @Override
    public Message clearAll() {
        System.gc();
        storageService.init();
        return new Message("done", SC_OK);
    }

    @Override
    public List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String shortName, int startNumber, int count) {
        if (conditions == null || conditions.isEmpty()) {
            return findByShortNameLike(shortName, startNumber, count);
        }
        Filter filter = new Filter(conditions);
        List<Coordinates> coords = daoCoordinates.findAll(filter);
        String shortNameTemplate = '%' + shortName + '%';
        List<Airfoil> airfoils = daoAirfoil.findDistinctAirfoilByCoordinatesInAndShortNameLike(coords, shortNameTemplate, new PageRequest(startNumber, count));
        for (Airfoil airfoil : airfoils) {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(airfoils);
    }

    @Override
    public List<AirfoilDTO> findByShortNameLike(String shortName, int startNumber, int count) {
        String shortNameTemplate;
        if ("null".equals(shortName)) {
            shortNameTemplate = "%";
        } else {
            shortNameTemplate = '%' + shortName + '%';
        }
        List<Airfoil> airfoils = daoAirfoil.findByShortNameLike(shortNameTemplate, new PageRequest(startNumber, count));
        for (Airfoil airfoil : airfoils) {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(airfoils);
    }

    @Override
    public int countByShortNameLike(String shortName) {
        String shortNameTemplate = '%' + shortName + '%';
        return daoAirfoil.countByShortNameLike(shortNameTemplate);
    }

    @Override
    public int countSearchAirfoil(List<Condition> conditions, String shortName) {
        if (conditions == null || conditions.isEmpty()) {
            return countByShortNameLike(shortName);
        }
        Filter filter = new Filter(conditions);
        List<Coordinates> coords = daoCoordinates.findAll(filter);
        String shortNameTemplate = '%' + shortName + '%';
        return daoAirfoil.countDistinctAirfoilByCoordinatesInAndShortNameLike(coords, shortNameTemplate);
    }

    private void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (daoMenuItem.findOneByUrlCode(String.valueOf(airfoil.getPrefix().getPrefix())) == null) {
            List<Menu> allMenu = daoMenu.findAll();
            for (Menu menu : allMenu) {
                if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                    MenuItem menuItem = converter.prefixToMenuItem(airfoil.getPrefix());
                    menu.getMenuItems().add(menuItem);
                    break;
                }
            }
            daoMenu.save(allMenu);
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
        List<Menu> allMenu = daoMenu.findAll();
        for (Menu menu : allMenu) {
            List<MenuItem> MenuItemsSorting = new ArrayList<>();
            MenuItemsSorting.addAll(menu.getMenuItems());
            Collections.sort(MenuItemsSorting, new Comparator<MenuItem>() {
                @Override
                public int compare(MenuItem o1, MenuItem o2) {
                    return o1.getUrlCode().charAt(0) - o2.getUrlCode().charAt(0);
                }
            });
            menu.setMenuItems(MenuItemsSorting);
        }
        return allMenu;
    }

    @PostConstruct
    public void init() {
//        List<Condition> conditions = Arrays.asList(new Condition(">", "maxClCd", "100"));
//        List<AirfoilDTO> airfoilDTOs = searchAirfoils(conditions, "%a%");
//        System.out.println(airfoilDTOs);
        try {
            if (!new File(configParserPath).exists()) {
                propertiesHandler.load(companiesXml.getInputStream());
            } else {
                propertiesHandler.load(configParserPath);
            }
        } catch (IOException e) {
            LOGGER.warn("Ошибка при попытке инициализировать настройки парсера", e);
        }
    }

    @Override
    public List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count) {
        List<Airfoil> airfoilsByPrefix = daoAirfoil.findByPrefixOrderByShortName(new Prefix(prefix), new PageRequest(startNumber, count));
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
                if (strings.length == 0) {
                    return;
                }
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                LOGGER.warn("Оштбка чтения файла", e);
                break;
            }
        }
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count) {
        List<Airfoil> allAirfoils = daoAirfoil.findAll(new PageRequest(startNumber, count));
        for (Airfoil airfoil : allAirfoils) {
            createDatFile(airfoil);
        }
        return allAirfoils;
    }

    @Override
    public List<String> updateGraf(String shortName, List<String> checkedList) {
        Airfoil airfoil = daoAirfoil.findOneByShortName(shortName);
        if (airfoil != null) {
            try {
                new BuilderGraphs(storageService).draw(airfoil, checkedList, true);
            } catch (Exception e) {
                LOGGER.warn("Ошибка при обработке файловк с координатами", e);
            }
        }
        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : CHART_NAMES) {
            imgCsvName.add("/files/chartTemp/" + shortName + chartName + ".png");
        }
        return imgCsvName;
    }

    @Override
    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = daoAirfoil.findOne(airfoilId);
        if (null == airfoil) {
            return null;
        }
        List<String> stlFileNames = null;
        try {
            new BuilderGraphs(storageService).draw(airfoil, null, false);
            stlFileNames = stlGenerator.generate(airfoil.getShortName(), airfoil.getCoordView(), storageService);
        } catch (Exception e) {
            LOGGER.warn("Ошибка при обработке файлов с координатами", e);
        }
        drawViewAirfoil(airfoil);
        return converter.airfoilToAirfoilDetail(airfoil, ServiceAirfoilTools.CHART_NAMES, stlFileNames);
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
            LOGGER.warn("Ошибка при рисовании графиков", e);
        }
    }

    @Override
    public Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        if (name.isEmpty()) {
            LOGGER.debug("Имя не должно быть пустым");
            return new Message("Имя не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        Airfoil airfoil = new Airfoil(name, details, shortName);
        if (daoAirfoil.exists(shortName)) {
            LOGGER.debug("Airfoil с таким именем уже существует, Выберите другое имя");
            return new Message("Airfoil с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        }
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    private Message addUpdateAirfoil(MultipartFile fileAirfoil, List<MultipartFile> files, Airfoil airfoil) {
        try {
            if (fileAirfoil == null || fileAirfoil.isEmpty()) {
                airfoil.setCoordView(daoAirfoil.findOneByShortName(airfoil.getShortName()).getCoordView());
            } else {
                airfoil.setCoordView(parseFileScv.parseFileAirfoil(fileAirfoil));
            }
            if (files == null || files.size() == 1 && files.get(0).isEmpty()) {
                airfoil.setCoordinates(daoAirfoil.findOneByShortName(airfoil.getShortName()).getCoordinates());
            } else {
                airfoil.setCoordinates(createCoordinateSet(files));
            }
            addMenuItemForNewAirfoil(airfoil);
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            LOGGER.warn("Один из файлов имеет не верный формат", e);
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
        storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    @Override
    public Message addAirfoil(AirfoilEdit airfoilEdit) {
        if (airfoilEdit.getShortName() == null || airfoilEdit.getShortName().isEmpty()) {
            LOGGER.debug("airfoil не добавлен - Короткое имя профиля не должно быть пустым");
            return new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
        }
        if (daoAirfoil.findOneByShortName(airfoilEdit.getShortName()) != null) {
            LOGGER.debug("Airfoil с таким именем уже существует, Выберите другое имя");
            return new Message("Airfoil с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        try {
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            LOGGER.warn("ошибка при добавлении в базу нового airfoil", e);
            return new Message("Ошибка при добавлении в базу нового airfoil", SC_CONFLICT);
        }
        return new Message("Airfoil успешно добавлен", SC_OK);
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        return daoAirfoil.countByPrefix(new Prefix(prefix));
    }

    private Set<Coordinates> createCoordinateSet(List<MultipartFile> files) throws IOException {
        Set<Coordinates> coordinates = new HashSet<>();
        for (MultipartFile file : files) {
            coordinates.add(new Coordinates(parseFileScv.csvToString(file.getInputStream()), file.getOriginalFilename()));
        }
        return coordinates;
    }
}
