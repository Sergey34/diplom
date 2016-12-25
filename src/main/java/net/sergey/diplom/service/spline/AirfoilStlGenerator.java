package net.sergey.diplom.service.spline;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AirfoilStlGenerator {

    private static final String REGEX = ",";
    private static final int b = 100;
    private static final StringBuilder FILE_HEADER = new StringBuilder();
    private static final StringBuilder FILE_FOOTER = new StringBuilder();


    static {
        FILE_HEADER.append("module airfoil(h) {\n\tlinear_extrude(height = h, convexity = 10, $fn = 200) {\n")
                .append("\t\tpolygon(\n\t\t\tpoints=[\n");
        FILE_FOOTER.append("\t\t\t],\n\t\tconvexity=10);\n\t}\n}\n\nairfoil(10, 0.2);\n");
    }


    public String generate(String fileName, String coordView, String PATH) throws IOException {
        String[] split1 = coordView.split("\n");
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (String line : split1) {
            try {
                String[] strings = line.trim().split(REGEX);
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        List<Double> t = new ArrayList<>(Collections.nCopies(x.size(), 0.0));
        for (int i = 1; i < x.size(); i++) {
            t.set(i, t.get(i - 1) + dist(i, i - 1, x, y));
        }

        Spline splineX = new Spline();
        splineX.BuildSpline(t, x, x.size());

        Spline splineY = new Spline();
        splineY.BuildSpline(t, y, x.size());
        List<Double> ySpline = new ArrayList<>();
        List<Double> xSpline = new ArrayList<>();
        spline(t, splineY, splineX, ySpline, xSpline);

        String stlFileName = PATH + "/" + fileName + '_' + b + ".scad";
        try (BufferedWriter scadWriter = new BufferedWriter(new FileWriter(stlFileName))) {
            scadWriter.write(FILE_HEADER.toString());
            for (int i = 0; i < xSpline.size(); i++) {
                scadWriter.write("\t\t\t\t[" + String.format("%.6e", xSpline.get(i)) + ", " +
                        String.format("%.6e", ySpline.get(i)) + "]\n");
            }
            scadWriter.write(FILE_FOOTER.toString());
            return fileName + '_' + b + ".scad";
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void spline(List<Double> t, Spline splineY, Spline splineX, List<Double> ySpline, List<Double> xSpline) {
        for (int i = 0; i < t.size() - 1; i++) {
            Double start = t.get(i);
            double v = (t.get(i + 1) - start) / 4;
            xSpline.add(splineX.calculateValue(start));
            ySpline.add(splineY.calculateValue(start));
            for (int j = 1; j < 4; j++) {
                xSpline.add(splineX.calculateValue(start + j * v));
                ySpline.add(splineY.calculateValue(start + j * v));
            }
        }
        xSpline.add(splineX.calculateValue(t.get(t.size() - 1)));
        ySpline.add(splineY.calculateValue(t.get(t.size() - 1)));

    }

    private Double dist(int j, int k, List<Double> x, List<Double> y) {
        return Math.sqrt(Math.pow((x.get(j) - x.get(k)), 2) + Math.pow((y.get(j) - y.get(k)), 2));
    }
}
