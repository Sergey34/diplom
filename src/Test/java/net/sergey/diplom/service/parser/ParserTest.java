package net.sergey.diplom.service.parser;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVWriter;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.ServletContext;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class ParserTest {
    @Autowired
    private ServletContext servletContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    @Test
    public void parseFileCSVtoJson() throws IOException {
        URL url = new URL("http://airfoiltools.com/polar/csv?polar=xf-a18-il-50000");
        String s = parser.csvToString(url);

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(servletContext.getRealPath("/resources/") + "/xf-a18-il-50000.csv"))) {
            CSVParser csvParser = new CSVParser();
            String[] csvLines = s.split("\n");
            String[] keys = csvParser.parseLine(csvLines[10]);
            Map<String, List<Double>> coordinates = generateMapping(keys);


            for (int j = 11; j < csvLines.length; j++) {
                String[] strings = csvParser.parseLine(csvLines[j]);
                csvWriter.writeNext(strings);
                for (int i = 0; i < strings.length; i++) {
                    coordinates.get(keys[i]).add(Double.parseDouble(strings[i]));
                }
            }
            System.out.println(coordinates);
        } catch (NumberFormatException | IOException e) {
            LOGGER.warn("невалидный файл!!! ");
            throw e;
        }


        System.out.println(s);
    }

    private Map<String, List<Double>> generateMapping(String[] keys) {
        HashMap<String, List<Double>> coordinates = new HashMap<>();
        for (String key : keys) {
            coordinates.put(key, new ArrayList<>());
        }
        return coordinates;
    }

    @Autowired
    Parser parser;

    @Test
    public void downloadDetailInfo() throws Exception {
        Set<Coordinates> coordinates = parser.downloadDetailInfo("goe280-il");
        //Set<Coordinates> coordinates = parser.downloadDetailInfo("goe279-il");
        System.out.println(coordinates);
    }

    @Test
    public void initTest() throws Exception {
        Assert.assertTrue(parser != null);
        Assert.assertTrue(parser.dao != null);
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


    @Test
    public void initTest4() throws Exception {
        String s = "S s1010 to supermarine371ii (174)";


    }


}