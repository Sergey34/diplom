package net.sergey.diplom.service.parser;

import au.com.bytecode.opencsv.CSVReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
@WebAppConfiguration
public class ParserTest {
    @Autowired
    Parser parser;

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
        URL urlFile = new URL("http://airfoiltools.com/polar/csv?polar=xf-a18-il-50000");


        Coordinates Coordinates;
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(urlFile.openStream()), ',', '\'', 10)) {
            List<String[]> strings = csvReader.readAll();
            Coordinates = new Coordinates(new HashMap<>(), strings.get(0));
            for (int i = 0; i < strings.get(0).length; i++) {
                for (int j = 1; j < strings.size(); j++) {
                    Coordinates.getCoordinates().get(strings.get(0)[i]).add(Double.parseDouble(strings.get(j)[i]));
                }
            }
        }
    }


}