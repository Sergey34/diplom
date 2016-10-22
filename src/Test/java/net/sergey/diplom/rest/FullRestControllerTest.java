package net.sergey.diplom.rest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class FullRestControllerTest {
    private static final String BASE_64_LOGIN = "YWxleDoxMjM0NTY=";

    @Test
    public void getA() throws Exception {
        Document document = Jsoup.connect("http://localhost:8082/rest/geta").
                header("Authorization", "Basic " + BASE_64_LOGIN).ignoreContentType(true).get();
        System.out.println(document);
    }

}