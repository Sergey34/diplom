package net.sergey.diplom.service;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BuilderFiles {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    private final String PATH;

    public BuilderFiles(String path) {
        this.PATH = path;
    }

    public void draw(final Airfoil airfoil) throws Exception {
        XYChart chartClCd = getXYChart().title("Cl v Cd").xAxisTitle("Cd").yAxisTitle("Cl").build();
        XYChart chartClAlpha = getXYChart().title("Cl v Alpha").xAxisTitle("Alpha").yAxisTitle("Cl").build();
        XYChart chartCdAlpha = getXYChart().title("Alpha v Cd").xAxisTitle("Cd").yAxisTitle("Alpha").build();
        XYChart chartCmAlpha = getXYChart().title("Alpha v Cm").xAxisTitle("Cm").yAxisTitle("Alpha").build();
        XYChart chartClCdAlpha = getXYChart().title("Cl|Cd v Alpha").xAxisTitle("Alpha").yAxisTitle("Cl/Cd").build();
        fillXYChart(airfoil, chartClCd, chartClAlpha, chartCdAlpha, chartCmAlpha, chartClCdAlpha);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future> futures = new ArrayList<>();
        for (XYChart xyChart : Arrays.asList(chartClCd, chartClAlpha, chartCdAlpha, chartCmAlpha, chartClCdAlpha)) {
            Future<?> submit = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    saveBitmap(airfoil, xyChart);
                }
            });
            futures.add(submit);
        }
        for (Future future : futures) {
            futureWait(future);
        }
    }

    private XYChartBuilder getXYChart() {
        return new XYChartBuilder().width(700).height(400);
    }

    private void futureWait(Future future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap(final Airfoil airfoil, XYChart chartClCd) {
        try {
            BitmapEncoder.saveBitmap(chartClCd, PATH + "/chartTemp/" + airfoil.getId() + chartClCd.getTitle(), BitmapEncoder.BitmapFormat.BMP);
        } catch (IOException e) {
            LOGGER.warn("не удалось сохранить график {} для {}\n{}", chartClCd.getTitle(), airfoil.getImage(), Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    private void fillXYChart(final Airfoil airfoil, XYChart chartClCd, XYChart chartClAlpha, XYChart chartCdAlpha, XYChart chartCmAlpha, XYChart chartClCdAlpha) {
        for (Coordinates coordinates : airfoil.getCoordinates()) {
            Map<String, List<Double>> map = parseStrCSVtoMap(coordinates.getCoordinatesJson(), coordinates.getFileName());
            if (map == null) {
                return;
            }
            chartClCd.addSeries(coordinates.getFileName(), map.get("Cd"), map.get("Cl")).setMarker(SeriesMarkers.NONE);
            chartClAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cl")).setMarker(SeriesMarkers.NONE);
            List<Double> clDivCd = divListValue(map.get("Cl"), map.get("Cd"));
            chartClCdAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), clDivCd).setMarker(SeriesMarkers.NONE);
            chartCdAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cd")).setMarker(SeriesMarkers.NONE);
            chartCmAlpha.addSeries(coordinates.getFileName(), map.get("Alpha"), map.get("Cm")).setMarker(SeriesMarkers.NONE);
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
