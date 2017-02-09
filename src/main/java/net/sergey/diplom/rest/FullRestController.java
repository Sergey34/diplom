package net.sergey.diplom.rest;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.dto.UserDto;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.service.ServiceInt;
import net.sergey.diplom.service.storageservice.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RequestMapping(value = "/rest")
@RestController
public class FullRestController {
    private final ServiceInt service;
    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    public FullRestController(ServiceInt service) {
        this.service = service;
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public List<Menu> getMenu() throws IOException {
        return service.getMenu();
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDto getUserInfo() {
        return service.getCurrentUserInfo();
    }

    @RequestMapping(value = "/userRoles", method = RequestMethod.GET)
    public List<Authorities> getUserRole() throws IOException {
        return service.getAllUserRoles();
    }

    @RequestMapping(value = "/airfoilsDto/{prefix}/{startNumber}/{count}", method = RequestMethod.GET)
    public List<AirfoilDTO> getAirfoilsDtoByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsDtoByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/airfoils/{prefix}/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAirfoilByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/airfoilsDto/{startNumber}/{count}", method = RequestMethod.GET)
    public List<AirfoilDTO> getAllAirfoilDto(@PathVariable int startNumber, @PathVariable int count) {
        return service.getAllAirfoilDto(startNumber, count);
    }

    @RequestMapping(value = "/airfoils/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAllAirfoilByPrefix(@PathVariable int startNumber, @PathVariable int count) {
        return service.getAllAirfoils(startNumber, count);
    }

    @RequestMapping(value = "/airfoilDto/{airfoilId}", method = RequestMethod.GET)
    public AirfoilDetail getDetailInfo(@PathVariable String airfoilId) {
        AirfoilDetail detailInfo = service.getDetailInfo(airfoilId);
        try {
            String s = storageService.listUploadedFiles("/airfoil_img/" + airfoilId);
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return detailInfo;
    }

    @RequestMapping(value = "/airfoil/{airfoilId}", method = RequestMethod.GET)
    public Airfoil getAirfoilById(@PathVariable String airfoilId) {
        return service.getAirfoilById(airfoilId);
    }

    @RequestMapping(value = "/countAirfoil/{prefix}", method = RequestMethod.GET)
    public int getCountAirfoil(@PathVariable char prefix) {
        return service.getCountAirfoilByPrefix(prefix);
    }

    @RequestMapping(value = "/updateGraf/{airfoilId}", method = RequestMethod.POST)
    public List<String> updateGraf(@PathVariable String airfoilId, @RequestBody List<String> checkedList) {
        return service.updateGraf(airfoilId, checkedList);
    }
}
