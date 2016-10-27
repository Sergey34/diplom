package net.sergey.diplom.service.utils;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.MenuItem;

import java.util.List;

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
}
