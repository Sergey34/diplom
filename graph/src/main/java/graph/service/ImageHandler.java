package graph.service;


import base.UtilsLogger;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImageHandler implements Callable<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private static String CATALOG;
    private static String PATH;
    private final List<Xy> xyList;
    private final String title;
    private final String xAlisa;
    private final String yAlisa;
    private final String fileName;
    private StyleXYChart style;

    public ImageHandler(String xAlisa, String yAlisa, String id, StyleXYChart style) {
        xyList = new ArrayList<>();
        this.xAlisa = xAlisa;
        this.yAlisa = yAlisa;
        this.title = xAlisa + " v " + yAlisa;
        this.fileName = id + title;
        this.style = style;
    }


    public ImageHandler(String shortName, Xy xy, StyleXYChart style) {
        this.xyList = new ArrayList<>();
        this.xAlisa = " ";
        this.yAlisa = " ";
        this.title = " ";
        this.fileName = shortName;
        this.xyList.add(xy);
        this.style = style;
    }

    public static void setSavePath(String path, String catalog) {
        PATH = path;
        CATALOG = catalog;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public Void call() {
        XYChart chart = style.getXYChart();
        chart.setTitle(title);
        chart.setXAxisTitle(xAlisa);
        chart.setYAxisTitle(yAlisa);
        for (Xy xy : xyList) {
            List<Double> x = xy.getX();
            List<Double> y = xy.getY();
            chart.addSeries(xy.getLegend(), x, y).setMarker(SeriesMarkers.NONE);
        }
        try {
            BitmapEncoder.saveBitmapWithDPI(chart, PATH + CATALOG + fileName,
                    BitmapEncoder.BitmapFormat.PNG, 80);
        } catch (Exception e) {
            LOGGER.warn("не удалось сохранить график {}", chart.getTitle());
        }
        return null;
    }

    public void add(Xy xy) {
        this.xyList.add(xy);
    }

    public void draw() {
        call();
    }
}