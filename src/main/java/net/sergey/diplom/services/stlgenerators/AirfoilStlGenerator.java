package net.sergey.diplom.services.stlgenerators;

import lombok.extern.slf4j.Slf4j;
import net.sergey.diplom.services.stlgenerators.bezierinterpolation.Point2D;
import net.sergey.diplom.services.storageservice.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
public abstract class AirfoilStlGenerator {
    private static final String REGEX = ",";
    private static final int b = 100;
    private static final StringBuilder FILE_HEADER = new StringBuilder();
    private static final StringBuilder FILE_FOOTER = new StringBuilder();


    static {
        FILE_HEADER.append("module airfoil(h) {\n\tlinear_extrude(height = h, convexity = 10, $fn = 200) {\n")
                .append("\t\tpolygon(\n\t\t\tpoints=[\n");
        FILE_FOOTER.append("\t\t\t],\n\t\tconvexity=10);\n\t}\n}\n\nairfoil(10, 0.2);\n");
    }

    @Value("#{'${interpolation}'.split(', ?')}")
    private List<String> beanNames;

    @Autowired
    private FileSystemStorageService storageService;


    private boolean allBeanNameExist(ApplicationContext context, List<String> beanNames) {
        for (String beanName : beanNames) {
            if (!context.containsBeanDefinition(beanName)) {
                return false;
            }
        }
        return true;
    }

    public List<String> generate(String airfoilName, String coordView) {
        if (coordView == null) {return new ArrayList<>();}
        String[] split1 = coordView.split("\n");
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        for (String line : split1) {
            try {
                String[] strings = line.trim().split(REGEX);
                x.add(Double.parseDouble(strings[0]) * b);
                y.add(Double.parseDouble(strings[strings.length - 1]) * b);
            } catch (Exception e) {
                log.warn("Ошибка генерации STL файлв", e);
                throw e;
            }
        }
        List<String> fileNames = new ArrayList<>();
        List<Interpolation> interpolators = getInterpolators(beanNames);

        for (Interpolation interpolator : interpolators) {
            List<Point2D> spline = interpolator.buildSplineForLists(x, y).applySpline();

            String fileName = airfoilName + '_' + interpolator.getName() + "_" + b + ".scad";
            String stlFileName = storageService.getRootLocation() + "/scadFiles/" + fileName;
            try (BufferedWriter scadWriter = new BufferedWriter(new FileWriter(stlFileName))) {
                scadWriter.write(FILE_HEADER.toString());
                for (Point2D point2D : spline) {
                    scadWriter.write("\t\t\t\t[" + String.format(Locale.ENGLISH, "%.6e", point2D.getX()) + ", " +
                            String.format(Locale.ENGLISH, "%.6e", point2D.getY()) + "],\n");
                }
                scadWriter.write(FILE_FOOTER.toString());
            } catch (IOException e) {
                log.warn("Ошибка генерации STL файлв", e);
            }
            fileNames.add(fileName);
        }
        return fileNames;
    }

    protected abstract List<Interpolation> getInterpolators(List<String> beanName);
}
