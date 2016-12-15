package net.sergey.diplom.rest;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilDTO;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.service.ServiceInt;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping(value = "/rest")
@RestController
public class FullRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final ServiceInt service;

    @Autowired
    public FullRestController(ServiceInt service) {
        this.service = service;
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public List<Menu> getMenu() throws IOException {
        return service.getMenu();
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String getUserInfo() {
        return service.getUserInfo();
    }

    @RequestMapping(value = "/userRoles", method = RequestMethod.GET)
    public List<UserRole> getUserRole() throws IOException {
        return service.getAllUserRoles();
    }

    @RequestMapping(value = "/getContext/{prefix}/{startNumber}/{count}", method = RequestMethod.GET)
    public List<AirfoilDTO> getAirfoilByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/getContext/allAirfoil/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAirfoilByPrefix(@PathVariable int startNumber, @PathVariable int count) {
        return service.getAllAirfoils(startNumber, count);
    }

    @RequestMapping(value = "/getDetailInfo/{airfoilId}", method = RequestMethod.GET)
    public AirfoilDetail getDetailInfo(@PathVariable int airfoilId) {
        return service.getDetailInfo(airfoilId);
    }

    @RequestMapping(value = "/getCountAirfoil/{prefix}", method = RequestMethod.GET)
    public int getCountAirfoil(@PathVariable char prefix) {
        return service.getCountAirfoilByPrefix(prefix);
    }

    @RequestMapping(value = "/updateGraf/{airfoilId}", method = RequestMethod.POST)
    public List<String> updateGraf(@PathVariable int airfoilId, @RequestBody List<String> checkedList) {
        return service.updateGraf(airfoilId, checkedList);
    }


}
