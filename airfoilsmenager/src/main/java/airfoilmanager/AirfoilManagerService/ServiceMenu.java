package airfoilmanager.AirfoilManagerService;

import airfoilmanager.dao.MenuDao;
import airfoilmanager.dao.MenuItemDao;
import base.dto.MenuDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ServiceMenu {
    private final MenuDao menuDao;
    private final MenuItemDao menuItemDao;
    private final Converter converter;

    @Autowired(required = false)
    public ServiceMenu(MenuDao menuDao, MenuItemDao menuItemDao, Converter converter) {
        this.menuDao = menuDao;
        this.menuItemDao = menuItemDao;
        this.converter = converter;
    }

    public List<MenuDto> getMenu() {
        return menuDao.findAll().stream().map(converter::toDtoMenu).collect(Collectors.toList());

    }

    public String deleteMenuItem(String menuItemName) {
        try {
            menuItemDao.deleteByName(menuItemName);
            return "Ok";
        } catch (Exception e) {
            log.warn("Ошибка при удалении пункта меню {}", menuItemName, e);
            return "NOT_OK";
        }
    }
}
