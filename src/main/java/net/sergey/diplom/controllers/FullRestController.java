package net.sergey.diplom.controllers;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.services.mainservice.ServiceAirfoil;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RequestMapping(value = "/rest")
@RestController
public class FullRestController {
    private final ServiceAirfoil service;
    @Autowired
    private UserService userService;

    @Autowired
    public FullRestController(ServiceAirfoil service) {
        this.service = service;
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public List<Menu> getMenu() throws IOException {
        return service.getMenu();
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDto getUserInfo() {
        return userService.getCurrentUserInfo();
    }

    @RequestMapping(value = "/userRoles", method = RequestMethod.GET)
    public List<Authorities> getUserRole() throws IOException {
        return userService.getAllUserRoles();
    }

    @RequestMapping(value = "/airfoilsDto/{prefix}/{startNumber}/{count}", method = RequestMethod.POST)
    public List<AirfoilDTO> getAirfoilsDtoByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsDtoByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/airfoils/{prefix}/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAirfoilByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/airfoilsDto/{startNumber}/{count}", method = RequestMethod.POST)
    public List<AirfoilDTO> getAllAirfoilDto(@PathVariable int startNumber, @PathVariable int count) {
        return service.getAllAirfoilDto(startNumber, count);
    }

    @RequestMapping(value = "/airfoils/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAllAirfoilByPrefix(@PathVariable int startNumber, @PathVariable int count) {
        return service.getAllAirfoils(startNumber, count);
    }

    @RequestMapping(value = "/airfoilDto/{airfoilId}", method = RequestMethod.GET)
    public AirfoilDetail getDetailInfo(@PathVariable String airfoilId) {
        return service.getDetailInfo(airfoilId);
    }

    @RequestMapping(value = "/airfoil/{airfoilId}", method = RequestMethod.GET)
    public Airfoil getAirfoilById(@PathVariable String airfoilId) {
        return service.getAirfoilById(airfoilId);
    }

    @RequestMapping(value = "/countAirfoil/{prefix}", method = RequestMethod.POST)
    public int getCountAirfoil(@PathVariable char prefix) {
        return service.getCountAirfoilByPrefix(prefix);
    }

    @RequestMapping(value = "/updateGraf/{airfoilId}", method = RequestMethod.POST)
    public List<String> updateGraf(@PathVariable String airfoilId, @RequestBody List<String> checkedList) {
        return service.updateGraf(airfoilId, checkedList);
    }

    @RequestMapping(value = "/searchByShortNameLike/{shortName}/{startNumber}/{count}", method = RequestMethod.POST)
    public List<AirfoilDTO> searchByShortNameLike(@PathVariable String shortName, @PathVariable int startNumber, @PathVariable int count) {
        return service.findByShortNameLike(shortName, startNumber, count);
    }

    @RequestMapping(value = "/countByShortNameLike/{shortName}", method = RequestMethod.POST)
    public int countByShortNameLike(@PathVariable String shortName) {
        return service.countByShortNameLike(shortName);
    }

    @RequestMapping(value = {"/searchAirfoil/{shortName}/{startNumber}/{count}"}, method = RequestMethod.POST)
    public List<AirfoilDTO> searchAirfoilsForName(@RequestBody List<Condition> conditions, @PathVariable String shortName, @PathVariable int startNumber, @PathVariable int count) {
        return service.searchAirfoils(conditions, shortName, startNumber, count);
    }

    @RequestMapping(value = {"/searchAirfoil/{startNumber}/{count}"}, method = RequestMethod.POST)
    public List<AirfoilDTO> searchAirfoils(@RequestBody List<Condition> conditions, @PathVariable int startNumber, @PathVariable int count) {
        return service.searchAirfoils(conditions, "", startNumber, count);
    }

    @RequestMapping(value = {"/countSearchAirfoil"}, method = RequestMethod.POST)
    public int countSearchAirfoil(@RequestBody List<Condition> conditions) {
        return service.countSearchAirfoil(conditions, "");
    }

    @RequestMapping(value = {"/countSearchAirfoil/{shortName}"}, method = RequestMethod.POST)
    public int countSearchAirfoilsForName(@RequestBody List<Condition> conditions, @PathVariable String shortName) {
        return service.countSearchAirfoil(conditions, shortName);
    }


}
