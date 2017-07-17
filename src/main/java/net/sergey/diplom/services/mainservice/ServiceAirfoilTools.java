package net.sergey.diplom.services.mainservice;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sergey.diplom.dao.Filter;
import net.sergey.diplom.dao.airfoil.DaoAirfoil;
import net.sergey.diplom.dao.airfoil.DaoCharacteristics;
import net.sergey.diplom.dao.menu.DaoMenu;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
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
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
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
    private final Filter filter;
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
                               Filter filter, DaoAirfoil daoAirfoil, DaoCharacteristics daoCharacteristics) {
        this.parseFileScv = parseFileScv;
        this.propertiesHandler = propertiesHandler;
        this.converter = converter;
        this.storageService = storageService;
        this.stlGenerator = stlGenerator;
        this.daoMenu = daoMenu;
        this.filter = filter;
        this.daoAirfoil = daoAirfoil;
        this.daoCharacteristics = daoCharacteristics;
    }


    @Override
    public Airfoil saveAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files) {
        Airfoil airfoil = Airfoil.builder().name(name).description(details).shortName(shortName).prefix(shortName.toUpperCase().charAt(0)).build();
        storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
        return addUpdateAirfoil(fileAirfoil, files, airfoil);
    }

    private Airfoil addUpdateAirfoil(MultipartFile fileAirfoil, List<MultipartFile> files, Airfoil airfoil) {
        Airfoil airfoilResult = daoAirfoil.findOneByShortName(airfoil.getShortName());
        Optional.ofNullable(airfoilResult).ifPresent(airfoil1 -> fillEmptyFieldsOldValue(fileAirfoil, files, airfoil, airfoil1));
        return add(airfoil);
    }

    @Override
    public Message saveAirfoil(AirfoilEdit airfoilEdit) {
        if (StringUtils.isEmpty(airfoilEdit.getShortName())) {
            log.debug("airfoil не добавлен/обновлен - Короткое имя профиля не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = getAirfoilByAirfoilEdit(airfoilEdit);
        if (add(airfoil) != null) {
            storageService.removeFiles(airfoil.getShortName(), CHART_NAMES);
            return ADD_AIRFOIL_CONFLICT;
        }
        return ASS_SUCCESS;
    }

    private Airfoil add(Airfoil airfoil) {
        try {
            addMenuItemForNewAirfoil(airfoil);
            daoCharacteristics.save(airfoil.getCharacteristics());
            daoAirfoil.save(airfoil);
            log.debug("Airfoil успешно добален / обновлен");
            return airfoil;
        } catch (Exception e) {
            log.warn("ошибка при добавлении / обновлении в базу нового airfoil", e);
            return null;
        }
    }

    @Override
    public Airfoil getAirfoilByShortName(String airfoilId) {
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
        Set<String> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(shortName);
        List<Airfoil> airfoils = daoAirfoil.findDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(ids, shortNameTemplate, new PageRequest(startNumber, count));
        generateFiles(airfoils);
        return converter.airfoilToAirfoilDto(airfoils);
    }

    @Override
    public List<AirfoilDTO> findByShortNameLike(String shortName, int startNumber, int count) {
        String shortNameTemplate = getShortNameTemplate(shortName);
        List<Airfoil> airfoils = daoAirfoil.findByShortNameRegex(shortNameTemplate, new PageRequest(startNumber, count));
        generateFiles(airfoils);
        return converter.airfoilToAirfoilDto(airfoils);
    }

    private void generateFiles(List<Airfoil> airfoils) {
        airfoils.forEach(airfoil -> {
            drawViewAirfoil(airfoil);
            createDatFile(airfoil);
        });
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
        Set<String> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(shortName);
        return daoAirfoil.countDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(ids, shortNameTemplate);
    }

    @Override
    public List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String template) {
        if (conditions == null || conditions.isEmpty()) {
            return findByShortNameLike(template, 0, Integer.MAX_VALUE);
        }
        Set<String> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(template);
        List<Airfoil> airfoils = daoAirfoil.findDistinctAirfoilByCharacteristics_fileNameInAndShortNameRegex(ids, shortNameTemplate);
        generateFiles(airfoils);
        return converter.airfoilToAirfoilDto(airfoils);
    }

    private void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (daoMenu.findMenuByItemsContains(converter.prefixToMenuItem(airfoil.getPrefix())) == null) {
            List<Menu> allMenu = daoMenu.findAll();
            if (allMenu.isEmpty()) {
                allMenu.add(Menu.builder().header(propertiesHandler.getProperty("menu_Header")).items(new ArrayList<>()).build());
            }
            for (Menu menu : allMenu) {
                if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                    MenuItem menuItem = converter.prefixToMenuItem(airfoil.getPrefix());
                    menu.getItems().add(menuItem);
                    break;
                }
            }
            daoMenu.save(allMenu);
        }
    }

    private Airfoil getAirfoilByAirfoilEdit(AirfoilEdit airfoilEdit) {
        Airfoil airfoilById = getAirfoilByShortName(airfoilEdit.getShortName());
        Set<Characteristics> characteristicsSet = airfoilEdit.getData().stream().map(data
                -> Characteristics.builder().coordinatesStl(data.getData()).fileName(data.getFileName())
                .renolgs(data.getReynolds()).nCrit(data.getNCrit()).maxClCd(data.getMaxClCd()).alpha(data.getAlpha()).build()).collect(Collectors.toSet());
        return Airfoil.builder()
                .id(airfoilById != null ? airfoilById.getId() : null)
                .name(airfoilEdit.getAirfoilName())
                .shortName(airfoilEdit.getShortName())
                .coordView(airfoilEdit.getViewCsv())
                .description(airfoilEdit.getDetails())
                .prefix(airfoilEdit.getShortName().toUpperCase().charAt(0))
                .characteristics(characteristicsSet).build();
    }

    @Override
    public List<Menu> getMenu() {
        List<Menu> allMenu = daoMenu.findAll();//// TODO: 15.07.17 сортировать в базе
        allMenu.forEach(menu -> menu.getItems().sort(Comparator.comparingInt(o -> o.getUrl().charAt(0))));
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
        List<Airfoil> airfoilsByPrefix = daoAirfoil.findByPrefixOrderByShortName(prefix, new PageRequest(startNumber, count));
        generateFiles(airfoilsByPrefix);
        return converter.airfoilToAirfoilDto(airfoilsByPrefix);
    }

    private void createDatFile(Airfoil airfoil) {
        String s = storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".dat";
        File file = new File(s);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && airfoil.getCoordView() != null) {
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
    public List<String> updateGraph(String shortName, List<String> checkedList) {
        Airfoil airfoilResult = daoAirfoil.findOneByShortName(shortName);
        String dir = getDirectory();
        Optional.ofNullable(airfoilResult).ifPresent(airfoil1 -> drawGraphs(checkedList, airfoil1, dir));
        return CHART_NAMES.stream().map(chartName -> "/files/chartTemp/" + dir + shortName + chartName + ".png").collect(Collectors.toList());
    }

    private String getDirectory() {
        return "/" + System.currentTimeMillis() + "/";
    }

    private void drawGraphs(List<String> checkedList, Airfoil airfoil1, String dir) {
        try {
            getBuilderGraphs().draw(airfoil1, checkedList, dir);
        } catch (Exception e) {
            log.warn("Ошибка при обработке файловк с координатами", e);
        }
    }

    @Lookup
    BuilderGraphs getBuilderGraphs() {
        return null;//proxy return new instance bean BuilderGraphs
    }

    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = daoAirfoil.findOneByShortName(airfoilId);
        if (null == airfoil) {
            throw new EmptyResultDataAccessException("не найден профиль " + airfoilId, 1);
        }
        String directory = getDirectory();
        drawGraphs(null, airfoil, directory);
        List<String> stlFileNames = stlGenerator.generate(airfoil.getShortName(), airfoil.getCoordView());
        drawViewAirfoil(airfoil);
        return converter.airfoilToAirfoilDetail(airfoil, ServiceAirfoilTools.CHART_NAMES, stlFileNames, directory);
    }

    private void drawViewAirfoil(Airfoil airfoil) {
        if (new File(storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
            log.info("изображение профиля airfoil {} уже существует", airfoil.getShortName());
            return;
        }
        if (StringUtils.isEmpty(airfoil.getCoordView())) {
            log.info("отсутствубт координаты для airfoil {}", airfoil.getShortName());
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
        return daoAirfoil.countByPrefix(prefix);
    }

    private Set<Characteristics> createCharacteristicsSet(List<MultipartFile> files) throws IOException {
        return files.stream().map(file -> Characteristics.builder().coordinatesStl(getCoordinatesStl(file))
                .fileName(file.getOriginalFilename()).build()).collect(Collectors.toSet());
    }

    @SneakyThrows
    private String getCoordinatesStl(MultipartFile file) {
        return parseFileScv.csvToString(file.getInputStream());
    }
}
