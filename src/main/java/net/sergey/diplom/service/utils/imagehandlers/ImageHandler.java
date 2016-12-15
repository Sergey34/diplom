package net.sergey.diplom.service.utils.imagehandlers;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImageHandler implements Callable<Object> {
    private static String CATALOG;
    private static String PATH;
    private final List<Xy> xyList;
    private final String title;
    private final String xAlisa;
    private final String yAlisa;
    private final String fileName;
    private StyleXYChart style;

    public ImageHandler(String xAlisa, String yAlisa, int id, StyleXYChart style) {
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

    @Override
    public Object call() throws Exception {
        XYChart chartClCd = style.getXYChart();
        chartClCd.setTitle(title);
        chartClCd.setXAxisTitle(xAlisa);
        chartClCd.setYAxisTitle(yAlisa);
        for (Xy xy : xyList) {
            List<Double> x = xy.getX();
            List<Double> y = xy.getY();
            chartClCd.addSeries(xy.getLegend(), x, y).setMarker(SeriesMarkers.NONE);
        }
        try {
            BitmapEncoder.saveBitmapWithDPI(chartClCd, PATH + CATALOG + fileName,
                    BitmapEncoder.BitmapFormat.PNG, 80);
        } catch (Exception e) {
            //LOGGER.warn("не удалось сохранить график {} для {}\n{}", chartClCd.getTitle(), airfoil.getImage(), Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    private XYChartBuilder getXYChart() {
        return new XYChartBuilder().width(700).height(400);
    }

    public void add(Xy xy) {
        this.xyList.add(xy);
    }

    public void setStyle(StyleXYChart style) {
        this.style = style;
    }

    public void draw() throws Exception {
        call();
    }
}