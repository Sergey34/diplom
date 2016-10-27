package net.sergey.diplom.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/WEB-INF/spring/root-context.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ServiceImplTest {
    @Autowired
    Service service;

    @Test
    public void saveSettings() throws Exception {

    }

    @Test
    public void loadSetting() throws Exception {

    }

    @Test
    public void showData() throws Exception {

    }

    @Test
    public void getBeanParserByName() throws Exception {

    }

    @Test
    public void getMenu() throws Exception {
        System.out.println("@@@@@@@@@@@@@@@@@@");
        System.out.println(service != null);
        System.out.println(service.getMenu());
    }

    @Test
    public void getUser() throws Exception {

    }

    @Test
    public void getAirfoilsByLitera() throws Exception {
        Document document = Jsoup.connect("http://airfoiltools.com/search/list?page=a&no=0").get();
        String content = document.body().getElementById("content").html();
        System.out.println(createString(content, "Page 1 of (.+)"));
        /*long start = System.nanoTime();
        for (int i = 0; i < 100_000; i++) {
            createString(content, "Page 5 of ([0-9]+).+");
        }
        long stop = System.nanoTime();
        System.out.println((stop-start)/100_000/1000);*/

    }

    private String createString(String item, String pattern) {
        Pattern pt = Pattern.compile(pattern);
        Matcher matcher = pt.matcher(item);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @Test
    public void getAirfoilsByLiteral() throws Exception {
        ServiceImpl service = new ServiceImpl(null, null);
        service.getAirfoilsByLiteral("A a18 to avistar (88)");
    }

}