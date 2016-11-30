package net.sergey.diplom.service.parser;

import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
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
        XYChart xyChart = new XYChart(880, 259);
        xyChart.getStyler().setAxisTicksVisible(false).setYAxisTicksVisible(false)
                .setLegendVisible(false);
        return xyChart;
    }

    @Test
    public void initTest4() throws Exception {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(
                        new URL("http://airfoiltools.com/airfoil/seligdatfile?airfoil=a63a108c-il").openStream()));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            String trim = line.trim();
            String[] split = trim.split(" +");
            if (isDoubleStr(split[0]) && isDoubleStr(split[split.length - 1])) {
                stringBuilder.append(split[0]).append(",").append(split[split.length - 1]).append('\n');
            }
        }
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        String s = stringBuilder.toString();
        String[] split = s.trim().split("\n");
        for (String line2 : split) {
            try {
                String[] strings = line2.trim().split(" ");
                x.add(Double.parseDouble(strings[0]));
                y.add(Double.parseDouble(strings[strings.length - 1]));
            } catch (Exception e) {
                LOGGER.warn("Оштбка чтения файла");
                e.printStackTrace();
                throw e;
            }
        }

        XYChart chartClCd = getXYChart();

        chartClCd.addSeries(" ", x, y).setMarker(SeriesMarkers.NONE).setShowInLegend(false);
        try {
            String fileName = servletContext.getRealPath("/resources/airfoil_img/") + "/qweq";
            BitmapEncoder.saveBitmap(chartClCd, fileName, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            LOGGER.warn("ошибка при сохранении");
            e.printStackTrace();
            throw e;
        }
    }

    private boolean isDoubleStr(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Test
    public void initTest44() throws Exception {
        Document detail = Jsoup.connect("http://airfoiltools.com/airfoil/details?airfoil=a18-il").timeout(10 * 1000).userAgent("Mozilla").ignoreHttpErrors(true).get();
        Elements polar = detail.getElementsByClass("polar");

        polar = polar.first().getElementsByTag("tr");
        Set<Coordinates> coordinates = new HashSet<>();
        for (Element element : polar) {
            Element reynolds = element.getElementsByClass("cell2").first();
            Element nCrit = element.getElementsByClass("cell3").first();
            Element maxClCd = element.getElementsByClass("cell4").first();
            Elements a = element.getElementsByClass("cell7").first().getElementsByTag("a");

            if (a.size() != 0) {
                //String fileName = createStringByPattern(a.attr("href"), GET_FILE_NAME_BY_URL_PATTERN);
                // URL urlFile = new URL(GET_FILE_CSV + fileName);
                // LOGGER.debug("url {}{}", GET_FILE_CSV, fileName);
                //coordinates.add(new Coordinates(csvToString(urlFile.openStream()), fileName + ".csv"));
            }
        }
    }

}