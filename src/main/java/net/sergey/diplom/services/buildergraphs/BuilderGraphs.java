package net.sergey.diplom.services.buildergraphs;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import net.sergey.diplom.services.buildergraphs.imagehandlers.ImageHandler;
import net.sergey.diplom.services.buildergraphs.imagehandlers.Xy;
import net.sergey.diplom.services.buildergraphs.imagehandlers.createxychartstyle.SimpleStyle;
import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuilderGraphs {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final ExecutorService executorService;
    private final FileSystemStorageService storageService;
    private Map<String, ImageHandler> imageHandler = new ConcurrentHashMap<>();

    {
        int n = Runtime.getRuntime().availableProcessors();
        if (n > 5) {
            executorService = Executors.newFixedThreadPool(5);
        } else {
            executorService = Executors.newFixedThreadPool(n);
        }
    }

    public BuilderGraphs(FileSystemStorageService storageService) {
        this.storageService = storageService;
        ImageHandler.setSavePath(this.storageService.getRootLocation() + "/chartTemp/");
    }

    public void draw(final Airfoil airfoil, List<String> checkedList, boolean updateFiles) throws Exception {
        imageHandler.put("chartClCd", new ImageHandler("Cl", "Cd", airfoil.getShortName(), new SimpleStyle()));
        imageHandler.put("chartClAlpha", new ImageHandler("Cl", "Alpha", airfoil.getShortName(), new SimpleStyle()));
        imageHandler.put("chartClCdAlpha", new ImageHandler("Cl div Cd", "Alpha", airfoil.getShortName(), new SimpleStyle()));
        imageHandler.put("chartCdAlpha", new ImageHandler("Cd", "Alpha", airfoil.getShortName(), new SimpleStyle()));
        imageHandler.put("chartCmAlpha", new ImageHandler("Cm", "Alpha", airfoil.getShortName(), new SimpleStyle()));
        if (!updateFiles) {
            filterHandler(imageHandler);
        }
        fillXYChart(airfoil, checkedList, updateFiles);
        executorService.invokeAll(imageHandler.values());
    }

    private void filterHandler(Map<String, ImageHandler> imageHandler) {
        for (Map.Entry<String, ImageHandler> item : imageHandler.entrySet()) {
            File file = new File(storageService.getRootLocation() + "/chartTemp/" + item.getValue().getFileName() + ".png");
            if (file.exists()) {
                imageHandler.remove(item.getKey());
            }
        }
    }

    private void fillXYChart(final Airfoil airfoil, List<String> checkedList, boolean updateFiles) {
        for (Characteristics characteristics : airfoil.getCharacteristics()) {
            Map<String, List<Double>> map;
            if (updateFiles || !fileExist(characteristics.getFileName())) {
                map = parseStrCSVtoMapSaveFile(characteristics.getcoordinatesStl(), characteristics.getFileName());
            } else {
                map = parseStrCSVtoMap(characteristics.getcoordinatesStl(), characteristics.getFileName());
            }
            if (map == null) {
                return;
            }
            if (isChecked(checkedList, characteristics)) {
                ImageHandler chartClCd = imageHandler.get("chartClCd");
                if (chartClCd != null) {
                    Xy ClCd = new Xy(map.get("Cd"), map.get("Cl"), characteristics.getFileName());
                    chartClCd.add(ClCd);
                }
                ImageHandler chartClAlpha = imageHandler.get("chartClAlpha");
                if (chartClAlpha != null) {
                    Xy ClAlpha = new Xy(map.get("Alpha"), map.get("Cl"), characteristics.getFileName());
                    chartClAlpha.add(ClAlpha);
                }
                ImageHandler chartClCdAlpha = imageHandler.get("chartClCdAlpha");
                if (chartClCdAlpha != null) {
                    List<Double> clDivCd = divListValue(map.get("Cl"), map.get("Cd"));
                    Xy ClCdAlpha = new Xy(map.get("Alpha"), clDivCd, characteristics.getFileName());
                    chartClCdAlpha.add(ClCdAlpha);
                }
                ImageHandler chartCdAlpha = imageHandler.get("chartCdAlpha");
                if (chartCdAlpha != null) {
                    Xy CdAlpha = new Xy(map.get("Alpha"), map.get("Cd"), characteristics.getFileName());
                    chartCdAlpha.add(CdAlpha);
                }
                ImageHandler chartCmAlpha = imageHandler.get("chartCmAlpha");
                if (chartCmAlpha != null) {
                    Xy CmAlpha = new Xy(map.get("Alpha"), map.get("Cm"), characteristics.getFileName());
                    chartCmAlpha.add(CmAlpha);
                }
            }
        }
    }

    private boolean fileExist(String fileName) {
        return new File(storageService.getRootLocation() + "/tmpCsv/" + fileName).exists();
    }

    private Map<String, List<Double>> parseStrCSVtoMap(String coordinateStr, String fileName) {
        Map<String, List<Double>> coordinates;
        try {
            CSVParser csvParser = new CSVParser();
            String[] csvLines = coordinateStr.split("\n");
            String[] keys = csvParser.parseLine(csvLines[10]);
            coordinates = generateMapping(keys);
            for (int j = 0; j < csvLines.length; j++) {
                String[] strings = csvParser.parseLine(csvLines[j]);
                if (j > 10) {
                    for (int i = 0; i < strings.length; i++) {
                        coordinates.get(keys[i]).add(Double.parseDouble(strings[i]));
                    }
                }
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn("невалидный файл!!! {}", fileName, e);
            return null;
        }
        return coordinates;
    }

    private boolean isChecked(List<String> checkedList, Characteristics characteristics) {
        if (checkedList == null) {
            return true;
        }
        for (String checked : checkedList) {
            if (characteristics.getFileName().equals(checked)) {
                return true;
            }
        }
        return false;
    }

    private List<Double> divListValue(List<Double> cl, List<Double> cd) {
        ArrayList<Double> clDivCd = new ArrayList<>();
        for (int i = 0; i < cl.size(); i++) {
            clDivCd.add(cl.get(i) / cd.get(i));
        }
        return clDivCd;
    }

    private Map<String, List<Double>> parseStrCSVtoMapSaveFile(String coordinateStr, String fileName) {
        Map<String, List<Double>> coordinates;
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(storageService.getRootLocation() + "/tmpCsv/" + fileName))) {
            CSVParser csvParser = new CSVParser();
            String[] csvLines = coordinateStr.split("\n");
            String[] keys = csvParser.parseLine(csvLines[10]);
            coordinates = generateMapping(keys);
            for (int j = 0; j < csvLines.length; j++) {
                String[] strings = csvParser.parseLine(csvLines[j]);
                csvWriter.writeNext(strings);
                if (j > 10) {
                    for (int i = 0; i < strings.length; i++) {
                        coordinates.get(keys[i]).add(Double.parseDouble(strings[i]));
                    }
                }
            }
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn("невалидный файл!!! {}", fileName, e);
            return null;
        }
        return coordinates;
    }

    private Map<String, List<Double>> generateMapping(String[] keys) {
        HashMap<String, List<Double>> coordinates = new HashMap<>();
        for (String key : keys) {
            coordinates.put(key, new ArrayList<Double>());
        }
        return coordinates;
    }
}
