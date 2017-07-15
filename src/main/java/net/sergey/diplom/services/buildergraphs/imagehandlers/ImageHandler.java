package net.sergey.diplom.services.buildergraphs.imagehandlers;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class ImageHandler implements Callable<Void> {
    private final List<Xy> xyList;
    private final String title;
    private final String xAlisa;
    private final String yAlisa;
    private final String fileName;
    private StyleXYChart style;
    private String dir;

    public ImageHandler(String xAlisa, String yAlisa, String id, StyleXYChart style, String dir) {
        xyList = new ArrayList<>();
        this.xAlisa = xAlisa;
        this.yAlisa = yAlisa;
        this.title = xAlisa + " v " + yAlisa;
        this.fileName = id + title;
        this.style = style;
        this.dir = dir;
    }


    public ImageHandler(String shortName, Xy xy, StyleXYChart style, String directory) {
        this.xyList = new ArrayList<>();
        this.xAlisa = " ";
        this.yAlisa = " ";
        this.title = " ";
        this.fileName = shortName;
        this.xyList.add(xy);
        this.style = style;
        this.dir = directory;
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
            BitmapEncoder.saveBitmapWithDPI(chart, dir + fileName,
                    BitmapEncoder.BitmapFormat.PNG, 80);
        } catch (Exception e) {
            log.warn("не удалось сохранить график {}", chart.getTitle(), e);
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