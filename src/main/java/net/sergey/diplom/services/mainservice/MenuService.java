package net.sergey.diplom.services.mainservice;

import net.sergey.diplom.dao.menu.DaoMenu;
import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.services.properties.PropertiesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class MenuService {
    private final DaoMenu daoMenu;
    private final Converter converter;
    private final PropertiesHandler propertiesHandler;

    @Autowired
    public MenuService(DaoMenu daoMenu, Converter converter, PropertiesHandler propertiesHandler) {
        this.daoMenu = daoMenu;
        this.converter = converter;
        this.propertiesHandler = propertiesHandler;
    }


    public void addMenuItemForNewAirfoil(Airfoil airfoil) {
        if (daoMenu.findMenuByItemsContains(converter.prefixToMenuItem(airfoil.getPrefix())) == null) {
            List<Menu> allMenu = daoMenu.findAll();
            if (allMenu.isEmpty()) {
                allMenu.add(Menu.builder().header(propertiesHandler.getProperty("menu_Header")).items(new ArrayList<>()).build());
            }
            for (Menu menu : allMenu) {
                if (menu.getHeader().equals(propertiesHandler.getProperty("menu_Header"))) {
                    MenuItem menuItem = converter.prefixToMenuItem(airfoil.getPrefix());
                    menu.getItems().add(menuItem);
                    break;
                }
            }
            daoMenu.save(allMenu);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Menu> getMenu() {
        List<Menu> allMenu = daoMenu.findAll();
        allMenu.forEach(menu -> menu.getItems().sort(Comparator.comparingInt(o -> o.getUrl().charAt(0))));
        return allMenu;
    }
}
