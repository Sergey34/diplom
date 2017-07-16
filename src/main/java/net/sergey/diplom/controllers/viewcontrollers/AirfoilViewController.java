package net.sergey.diplom.controllers.viewcontrollers;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.services.mainservice.ServiceAirfoil;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AirfoilViewController {
    private static final int COUNT_ON_PAGE = 21;
    private final ServiceAirfoil serviceAirfoil;
    private final UserService userService;

    @Autowired
    public AirfoilViewController(ServiceAirfoil serviceAirfoil, UserService userService) {
        this.serviceAirfoil = serviceAirfoil;
        this.userService = userService;
    }

    @GetMapping({"/airfoils/{prefix}/{page}", "/airfoils", "/airfoils/{prefix}", "/"})
    public String airfoils(Map<String, Object> model, @PathVariable(value = "prefix", required = false) String prefix,
                           @PathVariable(value = "page", required = false) Integer page) {
        prefix = Optional.ofNullable(prefix).orElse("A");
        page = Optional.ofNullable(page).orElse(0);
        List<AirfoilDTO> airfoils = serviceAirfoil.getAirfoilsDtoByPrefix(prefix.charAt(0), page, COUNT_ON_PAGE);
        fillMandatoryData(model);
        model.put("airfoils", airfoils);
        model.put("prefix", prefix);
        model.put("currentPage", page);
        int count = serviceAirfoil.getCountAirfoilByPrefix(prefix.charAt(0));
        if (count < COUNT_ON_PAGE) {count = COUNT_ON_PAGE;}
        model.put("pageCount", (int) Math.ceil(count * 1.0 / COUNT_ON_PAGE));
        return "airfoils";
    }


    @GetMapping("/airfoil/{shortName}")
    public String airfoil(Map<String, Object> model, @PathVariable(value = "shortName") String shortName) {
        AirfoilDetail airfoil = serviceAirfoil.getDetailInfo(shortName);
        model.put("airfoil", airfoil);
        fillMandatoryData(model);
        return "airfoil";
    }

    @ResponseBody
    @PostMapping(value = "/rest/updateGraph/{airfoilId}")
    public List<String> updateGraph(@PathVariable String airfoilId, @RequestBody List<String> checkedList) {
        return serviceAirfoil.updateGraph(airfoilId, checkedList);
    }

    @GetMapping("/add_airfoil")
    public String airfoil(Map<String, Object> model) {
        fillMandatoryData(model);
        return "add_airfoil";
    }

    @PostMapping(value = "/add_airfoil")
    public String addAirfoilForFileCsv(Map<String, Object> model, @RequestParam("files") List<MultipartFile> files,
                                       @RequestParam("name") String name,
                                       @RequestParam("ShortName") String shortName,
                                       @RequestParam("Details") String details,
                                       @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        Airfoil airfoil = serviceAirfoil.addAirfoil(shortName, name, details, fileAirfoil, files);
        if (airfoil != null) {
            return "redirect:/airfoil/" + airfoil.getShortName();
        }
        fillMandatoryData(model);
        model.put("added", false);
        return "add_airfoil";
    }


    private void fillMandatoryData(Map<String, Object> model) {
        UserDto currentUser = userService.getCurrentUserInfo();
        List<Menu> menu = serviceAirfoil.getMenu();
        model.put("user", currentUser);
        model.put("menu", menu);
    }

}
