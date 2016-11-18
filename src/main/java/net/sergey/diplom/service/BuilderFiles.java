package net.sergey.diplom.service;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.service.utils.UtilsLogger;
import net.sergey.diplom.service.utils.imagehandlers.ImageHandler;
import net.sergey.diplom.service.utils.imagehandlers.Xy;
import net.sergey.diplom.service.utils.imagehandlers.createxychartstyle.SimpleStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//// TODO: 18.11.16 переименовать как то связанным с графиками
public class BuilderFiles {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    private ImageHandler chartClCd;
    private ImageHandler chartClAlpha;
    private ImageHandler chartClCdAlpha;
    private ImageHandler chartCdAlpha;
    private ImageHandler chartCmAlpha;

    private String PATH;

    public BuilderFiles(String path) {
        ImageHandler.setSavePath(path, "/chartTemp/");
        PATH = path;
    }

    public void draw(final Airfoil airfoil) throws Exception {
        chartClCd = new ImageHandler("Cl", "Cd", airfoil.getId(), new SimpleStyle());
        chartClAlpha = new ImageHandler("Cl", "Alpha", airfoil.getId(), new SimpleStyle());
        chartClCdAlpha = new ImageHandler("Cl|Cd", "Alpha", airfoil.getId(), new SimpleStyle());
        chartCdAlpha = new ImageHandler("Cd", "Alpha", airfoil.getId(), new SimpleStyle());
        chartCmAlpha = new ImageHandler("Cm", "Alpha", airfoil.getId(), new SimpleStyle());
        fillXYChart(airfoil);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.invokeAll(Arrays.asList(chartClCd, chartClAlpha, chartClCdAlpha, chartCdAlpha, chartCmAlpha));
    }


    private void fillXYChart(final Airfoil airfoil) {
        for (Coordinates coordinates : airfoil.getCoordinates()) {
            Map<String, List<Double>> map = parseStrCSVtoMap(coordinates.getCoordinatesJson(), coordinates.getFileName());
            if (map == null) {
                return;
            }
            Xy ClCd = new Xy(map.get("Cd"), map.get("Cl"), coordinates.getFileName());
            chartClCd.add(ClCd);
            Xy ClAlpha = new Xy(map.get("Alpha"), map.get("Cl"), coordinates.getFileName());
            chartClAlpha.add(ClAlpha);
            List<Double> clDivCd = divListValue(map.get("Cl"), map.get("Cd"));
            Xy ClCdAlpha = new Xy(map.get("Alpha"), clDivCd, coordinates.getFileName());
            chartClCdAlpha.add(ClCdAlpha);
            Xy CdAlpha = new Xy(map.get("Alpha"), map.get("Cd"), coordinates.getFileName());
            chartCdAlpha.add(CdAlpha);
            Xy CmAlpha = new Xy(map.get("Alpha"), map.get("Cm"), coordinates.getFileName());
            chartCmAlpha.add(CmAlpha);
        }
    }

    private List<Double> divListValue(List<Double> cl, List<Double> cd) {
        ArrayList<Double> clDivCd = new ArrayList<>();
        for (int i = 0; i < cl.size(); i++) {
            clDivCd.add(cl.get(i) / cd.get(i));
        }
        return clDivCd;
    }

    private Map<String, List<Double>> parseStrCSVtoMap(String coordinateStr, String fileName) {
        Map<String, List<Double>> coordinates;
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(PATH + "/tmpCsv/" + fileName + ".csv"))) {
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
            LOGGER.warn("невалидный файл!!! {}\n{}", fileName, e.getMessage());
            return null;
        }
        return coordinates;
    }

    private Map<String, List<Double>> generateMapping(String[] keys) {
        HashMap<String, List<Double>> coordinates = new HashMap<>();
        for (String key : keys) {
            coordinates.put(key, new ArrayList<>());
        }
        return coordinates;
    }
}
