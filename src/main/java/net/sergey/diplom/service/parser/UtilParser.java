package net.sergey.diplom.service.parser;

import net.sergey.diplom.domain.airfoil.Links;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilParser {
    public static String getUrlMenuByTitle(List<Menu> menu, String literal) {
        for (Menu menuItem : menu) {
            for (MenuItem item : menuItem.getMenuItems()) {
                if (item.getName().equals(literal)) {
                    return item.getUrl();
                }
            }
        }
        throw new IllegalArgumentException("нет такого меню");
    }

    public static Set<Links> parseLinks(Elements links) {
        Set<Links> linksSet = new HashSet<>();
        for (Element link : links.first().getElementsByTag("a")) {
            Links linkItem = new Links(link.text(), link.attr("href"));
            //linkItem.setId(linkItem.hashCode());
            linksSet.add(linkItem);
        }
        return linksSet;
    }

    public static Set<Links> parseLinks(String link) {
        return parseLinks(Jsoup.parse(link).getAllElements());
    }
}
