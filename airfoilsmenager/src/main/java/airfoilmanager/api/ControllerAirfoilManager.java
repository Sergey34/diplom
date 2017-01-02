package airfoilmanager.api;

import airfoilmanager.AirfoilManagerService.Converter;
import airfoilmanager.AirfoilManagerService.ServiceAirfoil;
import airfoilmanager.AirfoilManagerService.ServiceMenu;
import base.domain.airfoil.Airfoil;
import base.domain.airfoil.Prefix;
import base.dto.AirfoilDto;
import base.dto.MenuDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ControllerAirfoilManager {
    private final ServiceAirfoil serviceAirfoil;
    private final ServiceMenu serviceMenu;
    private final Converter converter;

    @Autowired(required = false)
    public ControllerAirfoilManager(ServiceAirfoil serviceAirfoil, ServiceMenu serviceMenu, Converter converter) {
        this.serviceAirfoil = serviceAirfoil;
        this.serviceMenu = serviceMenu;
        this.converter = converter;
    }

    @RequestMapping("/getMenu")
    public List<MenuDto> getMenu() {
        return serviceMenu.getMenu();
    }

    @RequestMapping("/deleteMenu/{menuItemName}")
    public String deleteMenu(@PathVariable @NonNull String menuItemName) {
        return serviceMenu.deleteMenuItem(menuItemName);
    }

    @RequestMapping("/getAirfoil/{airfoilId}")
    public Airfoil getAirfoil(@PathVariable @NonNull String airfoilId) {
        return serviceAirfoil.getAirfoilById(airfoilId);
    }

    @RequestMapping("/getAirfoils/{prefix}")
    public List<AirfoilDto> getAirfoilsByPrefix(@PathVariable @NonNull String prefix) {
        List<Airfoil> airfoils = serviceAirfoil.getAirfoilsByPrefix(new Prefix(prefix.charAt(0)));
        return airfoils.stream().map(converter::toDtoAirfoil).collect(Collectors.toList());
    }

    @RequestMapping(value = "/addAirfoil", method = RequestMethod.POST)
    public String addAirfoil(@NonNull Airfoil airfoil) {
        return serviceAirfoil.addAirfoil(airfoil);
    }

    @RequestMapping(value = "/updateAirfoil", method = RequestMethod.POST)
    public String updateAirfoil(@NonNull Airfoil airfoil) {
        return serviceAirfoil.updateAirfoil(airfoil);
    }

    @RequestMapping(value = "/deleteAirfoil", method = RequestMethod.DELETE)
    public String deleteAirfoil(@NonNull String airfoilId) {
        return serviceAirfoil.deleteAirfoil(airfoilId);
    }

}
