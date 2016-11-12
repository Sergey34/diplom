package net.sergey.diplom.service.parser;

import net.sergey.diplom.service.utils.UtilsLogger;
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


    @Autowired
    Parser parser;

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


    @Test
    public void initTest4() throws Exception {
        String s = "S s1010 to supermarine371ii (174)";


    }


}