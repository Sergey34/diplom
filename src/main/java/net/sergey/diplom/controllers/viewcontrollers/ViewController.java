package net.sergey.diplom.controllers.viewcontrollers;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.services.mainservice.ServiceAirfoil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ViewController {
    public static final int COUNT_ON_PAGE = 21;
    @Autowired
    ServiceAirfoil serviceAirfoil;


    @GetMapping({"/airfoils/{prefix}/{page}", "/airfoils", "/airfoils/{prefix}"})
    public String airfoils(Map<String, Object> model, @PathVariable(value = "prefix", required = false) String prefix,
                           @PathVariable(value = "page", required = false) Integer page) {
        prefix = Optional.ofNullable(prefix).orElse("A");
        page = Optional.ofNullable(page).orElse(0);
        List<AirfoilDTO> airfoils = serviceAirfoil.getAirfoilsDtoByPrefix(prefix.charAt(0), page, COUNT_ON_PAGE);
        List<Menu> menu = serviceAirfoil.getMenu();
        model.put("airfoils", airfoils);
        model.put("menu", menu);
        model.put("prefix", prefix);
        model.put("currentPage", page);
        int count = serviceAirfoil.getCountAirfoilByPrefix(prefix.charAt(0));
        if (count < COUNT_ON_PAGE) {count = COUNT_ON_PAGE;}
        model.put("pageCount", (int) Math.ceil(count * 1.0 / COUNT_ON_PAGE));
        return "airfoilList";
    }

}
