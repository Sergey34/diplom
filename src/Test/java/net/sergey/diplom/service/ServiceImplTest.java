package net.sergey.diplom.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ServiceImplTest {
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