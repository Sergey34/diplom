package net.sergey.diplom.services.mainservice;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sergey.diplom.dao.Filter;
import net.sergey.diplom.dao.airfoil.DaoAirfoil;
import net.sergey.diplom.dao.airfoil.DaoCharacteristics;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
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
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static net.sergey.diplom.services.parser.consts.ConstantApi.*;

@Slf4j
@Service
public class ServiceAirfoilTools implements ServiceAirfoil {
    private static final List<String> CHART_NAMES = Arrays.asList(CL_V_CD, CL_V_ALPHA, CD_V_ALPHA, CM_V_ALPHA, CL_DIV_CD_V_ALPHA);
    private final ParseFileScv parseFileScv;
    private final PropertiesHandler propertiesHandler;
    private final Converter converter;
    private final MenuService menuService;
    private final FileSystemStorageService storageService;
    private final AirfoilStlGenerator stlGenerator;
    private final Filter filter;
    private final DaoAirfoil daoAirfoil;
    private final DaoCharacteristics daoCharacteristics;
    @Value("${config.parser.path}")
    private String configParserPath;
    @Value(value = "classpath:config.properties")
    private Resource companiesXml;

    @Autowired
    public ServiceAirfoilTools(@Qualifier("parser_service") ParseFileScv parseFileScv, PropertiesHandler propertiesHandler,
                               Converter converter, MenuService menuService, FileSystemStorageService storageService,
                               AirfoilStlGenerator stlGenerator, Filter filter, DaoAirfoil daoAirfoil, DaoCharacteristics daoCharacteristics) {
        this.parseFileScv = parseFileScv;
        this.propertiesHandler = propertiesHandler;
        this.converter = converter;
        this.menuService = menuService;
        this.storageService = storageService;
        this.stlGenerator = stlGenerator;
        this.filter = filter;
        this.daoAirfoil = daoAirfoil;
        this.daoCharacteristics = daoCharacteristics;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
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
    @Transactional(propagation = Propagation.REQUIRED)
    public Message saveAirfoil(AirfoilEdit airfoilEdit) {
        if (StringUtils.isEmpty(airfoilEdit.getShortName())) {
            log.debug("airfoil не добавлен/обновлен - Короткое имя профиля не должно быть пустым");
            return EMPTY_AIRFOIL_SHORT_NAME;
        }
        Airfoil airfoil = converter.getAirfoilByAirfoilEdit(airfoilEdit, getAirfoilByShortName(airfoilEdit.getShortName()));
        add(airfoil);
        return ASS_SUCCESS;
    }

    private Airfoil add(Airfoil airfoil) {
        menuService.addMenuItemForNewAirfoil(airfoil);
        daoCharacteristics.save(airfoil.getCharacteristics());
        daoAirfoil.save(airfoil);
        log.debug("Airfoil успешно добален / обновлен");
        return airfoil;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<AirfoilDTO> searchAirfoils(String shortName, int startNumber, int count) {
        return findByShortNameLike(shortName, startNumber, count);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public int countByShortNameLike(String shortName) {
        String shortNameTemplate = getShortNameTemplate(shortName);
        return daoAirfoil.countByShortNameRegex(shortNameTemplate);
    }

    private String getShortNameTemplate(String shortName) {
        return !"null".equals(shortName) ? shortName : "";
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public int countSearchAirfoil(String shortName) {
        return countByShortNameLike(shortName);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String template) {
        if (conditions == null || conditions.isEmpty()) {
            return findByShortNameLike(template, 0, Integer.MAX_VALUE);
        }
        Set<ObjectId> ids = daoCharacteristics.findCharacteristicsByTemplate(filter.toQuery(conditions));
        String shortNameTemplate = getShortNameTemplate(template);
        if (ids.isEmpty()) {return Collections.emptyList();}
        List<Airfoil> airfoils = daoAirfoil.findDistinctAirfoilByCharacteristics_idInAndShortNameRegex(ids, shortNameTemplate);
        generateFiles(airfoils);
        return converter.airfoilToAirfoilDto(airfoils);
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<String> updateGraph(String shortName, List<String> checkedList) {
        Airfoil airfoilResult = daoAirfoil.findOneByShortName(shortName);
        String dir = getDirectory();
        Optional.ofNullable(airfoilResult).ifPresent(airfoil1 -> getBuilderGraphs().draw(airfoil1, checkedList, dir));
        return CHART_NAMES.stream().map(chartName -> "/files/chartTemp/" + dir + shortName + chartName + ".png").collect(Collectors.toList());
    }

    private String getDirectory() {
        return "/" + System.currentTimeMillis() + "/";
    }

    @Lookup
    BuilderGraphs getBuilderGraphs() {
        return null;//proxy return new instance bean BuilderGraphs
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public AirfoilDetail getDetailInfo(String airfoilId) {
        Airfoil airfoil = daoAirfoil.findOneByShortName(airfoilId);
        if (null == airfoil) {
            throw new EmptyResultDataAccessException("не найден профиль " + airfoilId, 1);
        }
        String directory = getDirectory();
        getBuilderGraphs().draw(airfoil, null, directory);
        List<String> stlFileNames = stlGenerator.generate(airfoil.getShortName(), airfoil.getCoordView());
        drawViewAirfoil(airfoil);
        return converter.airfoilToAirfoilDetail(airfoil, ServiceAirfoilTools.CHART_NAMES, stlFileNames, directory);
    }

    private void drawViewAirfoil(Airfoil airfoil) {
        if (StringUtils.isEmpty(airfoil.getCoordView())) {
            log.info("отсутствубт координаты для airfoil {}", airfoil.getShortName());
            return;
        }
        if (new File(storageService.getRootLocation() + "/airfoil_img/" + airfoil.getShortName() + ".png").exists()) {
            log.info("изображение профиля airfoil {} уже существует", airfoil.getShortName());
            return;
        }

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        fillListXListY(x, y, airfoil.getCoordView().split("\n"));
        String directory = storageService.getRootLocation() + "/airfoil_img/";
        ImageHandler imageHandler = new ImageHandler(airfoil.getShortName(), new Xy(x, y, " "), new MinimalStyle(), directory);
        imageHandler.draw();
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
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
