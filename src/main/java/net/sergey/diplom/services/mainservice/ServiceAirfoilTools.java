package net.sergey.diplom.services.mainservice;


import lombok.extern.slf4j.Slf4j;
import net.sergey.diplom.dao.Filter;
import net.sergey.diplom.dao.airfoil.DaoAirfoil;
import net.sergey.diplom.dao.airfoil.DaoCharacteristics;
import net.sergey.diplom.dao.menu.DaoMenu;
import net.sergey.diplom.dao.menu.DaoMenuItem;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.services.buildergraphs.BuilderGraphs;
import net.sergey.diplom.services.buildergraphs.imagehandlers.ImageHandler;
import net.sergey.diplom.services.buildergraphs.imagehandlers.Xy;
import net.sergey.diplom.services.buildergraphs.imagehandlers.createxychartstyle.MinimalStyle;
import net.sergey.diplom.services.parser.ParseFileScv;
import net.sergey.diplom.services.properties.PropertiesHandler;
import net.sergey.diplom.services.stlgenerators.AirfoilStlGenerator;
import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static net.sergey.diplom.dto.messages.Message.*;

@Slf4j
@Service
public class ServiceAirfoilTools implements ServiceAirfoil {
    private static final Message ADD_AIRFOIL_CONFLICT = new Message("Ошибка при сохранени информации о профиле, возможно Airfoil с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
    private static final Message EMPTY_AIRFOIL_SHORT_NAME = new Message("Ошибка при добавлении в базу нового airfoil. Короткое имя профиля не должно быть пустым", SC_NOT_ACCEPTABLE);
    private static final Message ASS_SUCCESS = new Message("Airfoil успешно добален / обновлен", SC_OK);
    private static final Message DONE = new Message("done", SC_OK);
    private static final List<String> CHART_NAMES =
            Arrays.asList("Cl v Cd", "Cl v Alpha", "Cd v Alpha", "Cm v Alpha", "Cl div Cd v Alpha");
    private final ParseFileScv parseFileScv;
    private final PropertiesHandler propertiesHandler;
    private final Converter converter;
    private final FileSystemStorageService storageService;
    private final AirfoilStlGenerator stlGenerator;
    private final DaoMenu daoMenu;
    private final DaoMenuItem daoMenuItem;
    private final DaoAirfoil daoAirfoil;
    private final DaoCharacteristics daoCharacteristics;
    @Value("${config.parser.path}")
    private String configParserPath;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;

    @Autowired
    public ServiceAirfoilTools(ParseFileScv parseFileScv, PropertiesHandler propertiesHandler,
                               Converter converter, FileSystemStorageService storageService,
                               AirfoilStlGenerator stlGenerator, DaoMenu daoMenu,
                               DaoMenuItem daoMenuItem, DaoAirfoil daoAirfoil, DaoCharacteristics daoCharacteristics) {
        this.parseFileScv = parseFileScv;
        this.propertiesHandler = propertiesHandler;
        this.converter = converter;
        this.storageService = storageService;
        this.stlGenerator = stlGenerator;
        this.daoMenu = daoMenu;
        this.daoMenuItem = daoMenuItem;
        this.daoAirfoil = daoAirfoil;
        this.daoCharacteristics = daoCharacteristics;
    }


    @Override
    public Message updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        if (shortName == null || shortName.isEmpty()) {
            log.debug("Имя не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = Airfoil.builder().name(name).description(details).shortName(shortName).prefix(new Prefix(shortName.toUpperCase().charAt(0))).build();
        storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    @Override
    public Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        if (shortName == null || shortName.isEmpty()) {
            log.debug("Имя не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = Airfoil.builder().name(name).description(details).shortName(shortName).prefix(new Prefix(shortName.toUpperCase().charAt(0))).build();
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    @Override
    public Message addAirfoil(AirfoilEdit airfoilEdit) {
        if (airfoilEdit.getShortName() == null || airfoilEdit.getShortName().isEmpty()) {
            log.debug("airfoil не добавлен - Короткое имя профиля не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        if (add(airfoil)) {
            return ADD_AIRFOIL_CONFLICT;
        }
        return ASS_SUCCESS;
    }

    @Override
    public Message addAirfoil(Airfoil airfoil) {
        if (StringUtils.isEmpty(airfoil.getShortName())) {
            log.debug("airfoil не добавлен - Короткое имя профиля не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        if (add(airfoil)) {
            return ADD_AIRFOIL_CONFLICT;
        }
        return ASS_SUCCESS;
    }

    private boolean add(Airfoil airfoil) {
        try {
            daoCharacteristics.save(airfoil.getCharacteristics());
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            log.warn("ошибка при добавлении в базу нового airfoil", e);
            return true;
        }
        return false;
    }

    @Override
    public Message updateAirfoil(AirfoilEdit airfoilEdit) {
        if (StringUtils.isEmpty(airfoilEdit.getShortName())) {
            log.debug("airfoil не обновлен - Короткое имя профиля не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        addMenuItemForNewAirfoil(airfoil);
        try {
            daoCharacteristics.save(airfoil.getCharacteristics());
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            log.warn("Ошибка при обновлении airfoil {}", airfoil.getShortName(), e);
            return ADD_AIRFOIL_CONFLICT;
        }
        storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
        log.debug("Airfoil успешно обновлен {}", SC_OK);
        return ASS_SUCCESS;
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
        Optional.ofNullable(airfoilById).ifPresent(this::createDatFile);
        return airfoilById;
    }

    @Override
    public Message clearAll() {
        System.gc();
        storageService.init();
        return DONE;
    }

    @Override
    public List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String shortName, int startNumber, int count) {
        if (conditions == null || conditions.isEmpty()) {
            return findByShortNameLike(shortName, startNumber, count);
        }
        Filter filter = new Filter();
        Set<String> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(shortName);
        List<Airfoil> airfoils = daoAirfoil.findDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(ids, shortNameTemplate, new PageRequest(startNumber, count));
        for (Airfoil airfoil : airfoils) {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(airfoils);
    }

    @Override
    public List<AirfoilDTO> findByShortNameLike(String shortName, int startNumber, int count) {
        String shortNameTemplate = getShortNameTemplate(shortName);
        List<Airfoil> airfoils = daoAirfoil.findByShortNameRegex(shortNameTemplate, new PageRequest(startNumber, count));
        for (Airfoil airfoil : airfoils) {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        }
        return converter.airfoilToAirfoilDto(airfoils);
    }

    @Override
    public int countByShortNameLike(String shortName) {
        String shortNameTemplate = getShortNameTemplate(shortName);
        return daoAirfoil.countByShortNameRegex(shortNameTemplate);
    }

    private String getShortNameTemplate(String shortName) {
        return !"null".equals(shortName) ? shortName : "";
    }

    @Override
    public int countSearchAirfoil(List<Condition> conditions, String shortName) {
        if (conditions == null || conditions.isEmpty()) {
            return countByShortNameLike(shortName);
        }
        Filter filter = new Filter();
        Set<String> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(shortName);
        return daoAirfoil.countDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(ids, shortNameTemplate);
    }

    private void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (daoMenuItem.findOneByUrl(String.valueOf(airfoil.getPrefix().getPrefix())) == null) {
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
        Set<Characteristics> characteristicsSet = airfoilEdit.getData().stream().map(data
                -> Characteristics.builder().coordinatesStl(data.getData()).fileName(data.getFileName())
                .renolgs(data.getReynolds()).nCrit(data.getNCrit()).maxClCd(data.getMaxClCd()).build()).collect(Collectors.toSet());
        return Airfoil.builder()
                .name(airfoilEdit.getAirfoilName())
                .shortName(airfoilEdit.getShortName())
                .coordView(airfoilEdit.getViewCsv())
                .description(airfoilEdit.getDetails())
                .prefix(new Prefix(airfoilEdit.getShortName().toUpperCase().charAt(0)))
                .characteristics(characteristicsSet).build();
    }

    @Override
    public List<Menu> getMenu() {
        List<Menu> allMenu = daoMenu.findAll();//// TODO: 15.07.17 сортировать в базе
        allMenu.forEach(menu -> menu.getMenuItems().sort(Comparator.comparingInt(o -> o.getUrl().charAt(0))));
        return allMenu;
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
            log.warn("Ошибка при попытке инициализировать настройки парсера", e);
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
                if (file.createNewFile()) {
                    Files.write(file.toPath(), airfoil.getCoordView().getBytes());
                } else {
                    log.warn("Ошибка записи файла");
                }
            } catch (IOException e) {
                log.warn("Ошибка записи файла", e);
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
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                log.warn("Оштбка чтения файла", e);
                break;
            }
        }
    }

    @Override
    public List<Airfoil> getAllAirfoils(int startNumber, int count) {
        List<Airfoil> allAirfoils = daoAirfoil.findAll(new PageRequest(startNumber, count));
        allAirfoils.forEach(this::createDatFile);
        return allAirfoils;
    }

    @Override
    public List<String> updateGraph(String shortName, List<String> checkedList) {
        Airfoil airfoilResult = daoAirfoil.findOneByShortName(shortName);
        String dir = getDirectory();
        Optional.ofNullable(airfoilResult).ifPresent(airfoil1 -> drawGraphs(checkedList, airfoil1, dir));
        return CHART_NAMES.stream().map(chartName -> dir + shortName + chartName + ".png").collect(Collectors.toList());
    }

    private String getDirectory() {
        return System.currentTimeMillis() + "/";
    }

    private void drawGraphs(List<String> checkedList, Airfoil airfoil1, String dir) {
        try {
            new BuilderGraphs(storageService).draw(airfoil1, checkedList, dir);
        } catch (Exception e) {
            log.warn("Ошибка при обработке файловк с координатами", e);
        }
    }

    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = daoAirfoil.findOne(airfoilId);
        if (null == airfoil) {
            return null;
        }
        String directory = getDirectory();
        drawGraphs(null, airfoil, directory);
        List<String> stlFileNames = stlGenerator.generate(airfoil.getShortName(), airfoil.getCoordView(), storageService);
        drawViewAirfoil(airfoil);
        return converter.airfoilToAirfoilDetail(airfoil, ServiceAirfoilTools.CHART_NAMES, stlFileNames, directory);
    }

    private void drawViewAirfoil(Airfoil airfoil) {
        if (new File(storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
            return;
        }
        if (StringUtils.isEmpty(airfoil.getCoordView())) {
            log.warn("отсутствубт координаты для airfoil {}", airfoil.getShortName());
            return;
        }
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        fillListXListY(x, y, airfoil.getCoordView().split("\n"));
        String directory = storageService.getRootLocation() + "/airfoil_img/";
        ImageHandler imageHandler = new ImageHandler(airfoil.getShortName(), new Xy(x, y, " "), new MinimalStyle(), directory);
        try {
            imageHandler.draw();
        } catch (Exception e) {
            log.warn("Ошибка при рисовании графиков", e);
        }
    }

    private Message addUpdateAirfoil(MultipartFile fileAirfoil, List<MultipartFile> files, Airfoil airfoil) {
        Airfoil airfoilResult = daoAirfoil.findOneByShortName(airfoil.getShortName());
        Optional.ofNullable(airfoilResult).ifPresent(airfoil1 -> fillEmptyFieldsOldValue(fileAirfoil, files, airfoil, airfoil1));
        try {
            addMenuItemForNewAirfoil(airfoil);
            daoCharacteristics.save(airfoil.getCharacteristics());
            daoAirfoil.save(airfoil);
        } catch (Exception e) {
            log.warn("Ошибка при добалении профиля", e);
            return ADD_AIRFOIL_CONFLICT;
        }
        log.debug("Airfoil успешно добален / обновлен");
        return ASS_SUCCESS;
    }


    private void fillEmptyFieldsOldValue(MultipartFile fileAirfoil, List<MultipartFile> files, Airfoil airfoil, Airfoil airfoil1) {
        try {
            if (fileAirfoil == null || fileAirfoil.isEmpty()) {
                airfoil.setCoordView(airfoil1.getCoordView());
            } else {
                airfoil.setCoordView(parseFileScv.parseFileAirfoil(fileAirfoil));
            }
            if (files == null || files.size() == 1 && files.get(0).isEmpty()) {
                airfoil.setCharacteristics(airfoil1.getCharacteristics());
            } else {
                airfoil.setCharacteristics(createCharacteristicsSet(files));
            }
        } catch (Exception e) {
            log.warn("Ошибка чтения файла", e);
        }
    }

    @Override
    public int getCountAirfoilByPrefix(char prefix) {
        return daoAirfoil.countByPrefix(new Prefix(prefix));
    }

    private Set<Characteristics> createCharacteristicsSet(List<MultipartFile> files) throws IOException {
        Set<Characteristics> characteristics = new HashSet<>();
        for (MultipartFile file : files) {
            characteristics.add(Characteristics.builder().coordinatesStl(parseFileScv.csvToString(file.getInputStream())).fileName(file.getOriginalFilename()).build());
        }
        return characteristics;
    }
}
