package airfoilmanager.AirfoilManagerService;

import base.domain.airfoil.Airfoil;
import base.domain.menu.Menu;
import base.domain.menu.MenuItem;
import base.dto.AirfoilDto;
import base.dto.MenuDto;
import base.dto.MenuItemDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Converter {
    public AirfoilDto toDtoAirfoil(@NonNull Airfoil airfoil) {
        AirfoilDto airfoilDto = new AirfoilDto();
        airfoilDto.setName(airfoil.getName());
        airfoilDto.setShortName(airfoil.getShortName());
        airfoilDto.setDescription(airfoil.getDescription());
        airfoilDto.setPrefix(airfoil.getPrefix());
        airfoilDto.setCoordView(airfoil.getCoordView());
        return airfoilDto;
    }

    public MenuDto toDtoMenu(@NonNull Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setHeader(menu.getHeader());
        List<MenuItemDto> menuItemsDto = menu.getMenuItems().stream()
                .map(this::toDtoMenuItem).sorted(Comparator.comparing(MenuItemDto::getName)).
                        collect(Collectors.toList());
        menuDto.setMenuItems(menuItemsDto);
        return menuDto;
    }

    private MenuItemDto toDtoMenuItem(@NonNull MenuItem menuItem) {
        MenuItemDto menuItemDto = new MenuItemDto();
        menuItemDto.setName(menuItem.getName());
        menuItemDto.setUrlCode(menuItem.getUrlCode());
        return menuItemDto;
    }
}
