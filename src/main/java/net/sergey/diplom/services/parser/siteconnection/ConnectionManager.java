package net.sergey.diplom.services.parser.siteconnection;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

@Component
public class ConnectionManager {
    public Connection getJsoupConnect(String url, int timeout) {
        return Jsoup.connect(url).timeout(timeout).userAgent("Mozilla").ignoreHttpErrors(true);
    }
}
