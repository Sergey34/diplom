package net.sergey.diplom.controllers.viewcontrollers;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.services.mainservice.ServiceAirfoil;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
        model.put("url", "/airfoils/" + prefix);
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

    @ResponseBody
    @RequestMapping(value = "/rest/airfoilDto/{airfoilId}", method = RequestMethod.GET)
    public AirfoilDetail getDetailInfo(@PathVariable String airfoilId) {
        return serviceAirfoil.getDetailInfo(airfoilId);
    }

    @GetMapping("/add_airfoil")
    public String airfoil(Map<String, Object> model) {
        fillMandatoryData(model);
        return "add_airfoil";
    }

    @PostMapping(value = "/add_airfoil")
    public String addAirfoil(Map<String, Object> model, @RequestParam("files") List<MultipartFile> files,
                             @RequestParam("name") String name, @RequestParam("ShortName") String shortName,
                             @RequestParam("Details") String details, @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        Airfoil airfoil = serviceAirfoil.saveAirfoil(shortName, name, details, fileAirfoil, files);
        if (airfoil != null) {
            return "redirect:/airfoil/" + airfoil.getShortName();
        }
        fillMandatoryData(model);
        model.put("added", false);
        return "add_airfoil";
    }

    @GetMapping("/update_airfoil/{shortName}")
    public String updateAirfoil(Map<String, Object> model, @PathVariable("shortName") String shortName) {
        fillMandatoryData(model);
        AirfoilDetail airfoil = serviceAirfoil.getDetailInfo(shortName);
        model.put("airfoil", airfoil);
        return "edit_airfoil";
    }

    @PostMapping({"/search", "/search/{page}"})
    public String searchAirfoils(Map<String, Object> model,
                                 @RequestParam(value = "template", required = false) String template,
                                 @PathVariable(value = "page", required = false) Integer page) {
        fillMandatoryData(model);
        page = Optional.ofNullable(page).orElse(0);
        List<AirfoilDTO> airfoils = serviceAirfoil.searchAirfoils(null, template, page, COUNT_ON_PAGE);
        int pageCount = serviceAirfoil.countSearchAirfoil(null, template);
        model.put("airfoils", airfoils);
        model.put("url", "/search/");
        model.put("currentPage", page);
        model.put("pageCount", pageCount);
        return "airfoils";
    }

    @GetMapping({"/search_condition/{shortName}"})
    public String searchCondition(Map<String, Object> model, @PathVariable(value = "shortName", required = false) String template,
                                  @RequestParam("data") List<Condition> conditions) {
        fillMandatoryData(model);
        List<AirfoilDTO> airfoils = serviceAirfoil.searchAirfoils(conditions, template);
        int pageCount = serviceAirfoil.countSearchAirfoil(null, template);
        model.put("airfoils", airfoils);
        model.put("url", "/search/");
        model.put("pageCount", pageCount);
        model.put("conditions", new ArrayList<Condition>());
        return "airfoils";
    }

    @ResponseBody
    @PostMapping(value = "/store_airfoil")
    public Message updateAirfoilStringCsv(@RequestBody AirfoilEdit airfoilEdit) {
        return serviceAirfoil.saveAirfoil(airfoilEdit);
    }

    private void fillMandatoryData(Map<String, Object> model) {
        UserDto currentUser = userService.getCurrentUserInfo();
        List<Menu> menu = serviceAirfoil.getMenu();
        model.put("user", currentUser);
        model.put("menu", menu);
    }

}
