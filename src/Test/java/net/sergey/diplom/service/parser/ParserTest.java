package net.sergey.diplom.service.parser;

import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.Jsoup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class ParserTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    @Autowired
    Parser parser;
    @Autowired
    private ServletContext servletContext;

    @Test
    public void initTest() throws Exception {
        parser.init();
    }

    @Test
    public void initTest2() throws Exception {
        parser.parseMenu();
        System.out.println(Integer.MAX_VALUE);
    }

    @Test
    public void initTest3() throws Exception {
        String countPages = createDataByPattern("/search/list?page=b&no=0", "page=(.)");
        String countPages2 = createDataByPattern("Page 1 of 10 pages", "Page 1 of ([0-9]+).+");

        System.out.println(countPages);
        System.out.println(countPages2);
    }

    private String getCountPages(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "0";
    }

    private <T> T createDataByPattern(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return (T) matcher.group(1);
        }
        return (T) "a";
    }

    private XYChart getXYChart() {
        Styler.ChartTheme ggPlot2 = XYStyler.ChartTheme.Matlab;
        return new XYChart(780, 159, ggPlot2);
    }

    @Test
    public void initTest4() throws Exception {
        String file2 = Jsoup.connect("http://airfoiltools.com/airfoil/seligdatfile?airfoil=a18-il").timeout(0).userAgent("Mozilla")
                .get().body().text();

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(new URL("http://airfoiltools.com/airfoil/seligdatfile?airfoil=a18-il").openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.trim().split(" ");
            if (split.length == 3 && isDoubleStr(split[0]) && isDoubleStr(split[2])) {
                stringBuilder.append(line);
            }
        }

        System.out.println(stringBuilder.toString());

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        String[] split = file2.split(" ");
        for (int i = 0; i < split.length; i += 2) {
            if (isDoubleStr(split[i]) && isDoubleStr(split[i + 1])) {
                x.add(Double.parseDouble(split[i]));
                y.add(Double.parseDouble(split[i + 1]));
            }
        }
        XYChart chartClCd = getXYChart();

        chartClCd.addSeries("123", x, y).setMarker(SeriesMarkers.NONE).setShowInLegend(false);
        BitmapEncoder.saveBitmap(chartClCd, servletContext.getRealPath("/resources/") + "/chartTemp/" + "qwe", BitmapEncoder.BitmapFormat.BMP);

    }

    private boolean isDoubleStr(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


}