package net.sergey.diplom.services.stlgenerators.cubespline;

import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    static {
        FILE_HEADER.append("module airfoil(h) {\n\tlinear_extrude(height = h, convexity = 10, $fn = 200) {\n")
                .append("\t\tpolygon(\n\t\t\tpoints=[\n");
        FILE_FOOTER.append("\t\t\t],\n\t\tconvexity=10);\n\t}\n}\n\nairfoil(10, 0.2);\n");
    }

    public void generate(String fileName, String coordView, FileSystemStorageService storageService) throws IOException {
        String[] split1 = coordView.split("\n");
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (String line : split1) {
            try {
                String[] strings = line.trim().split(REGEX);
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                LOGGER.warn("Ошибка генерации STL файлв", e);
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

        List<Double> xSpline = splineX.applySpline(t, splineX);
        List<Double> ySpline = splineY.applySpline(t, splineY);

        String stlFileName = storageService.getRootLocation() + "/scadFiles/" + fileName + '_' + b + ".scad";
        try (BufferedWriter scadWriter = new BufferedWriter(new FileWriter(stlFileName))) {
            scadWriter.write(FILE_HEADER.toString());
            for (int i = 0; i < xSpline.size(); i++) {
                scadWriter.write("\t\t\t\t[" + String.format("%.6e", xSpline.get(i)) + ", " +
                        String.format("%.6e", ySpline.get(i)) + "]\n");
            }
            scadWriter.write(FILE_FOOTER.toString());
        } catch (IOException e) {
            LOGGER.warn("Ошибка генерации STL файлв", e);
            throw e;
        }
    }

    private Double dist(int j, int k, List<Double> x, List<Double> y) {
        return Math.sqrt(Math.pow((x.get(j) - x.get(k)), 2) + Math.pow((y.get(j) - y.get(k)), 2));
    }
}
