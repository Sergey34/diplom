package net.sergey.diplom.rest;

import net.sergey.diplom.domain.model.UserView;
import net.sergey.diplom.service.ServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping(value = "/rest/write")
@RestController(value = "write")
public class RestWriteController {
    private final ServiceInt service;

    @Autowired
    public RestWriteController(ServiceInt service) {
        this.service = service;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String addUser(@RequestBody UserView userView) {

        if (service.addUser(userView)) {
            return "{\"msg\":\"success\"}";
        }
        return "error";
    }

    @RequestMapping(value = "/addAirfoil", method = RequestMethod.POST)
    public String addAirfoil(@RequestParam("files") List<MultipartFile> files,
                             @RequestParam("name") String name,
                             @RequestParam("ShortName") String shortName,
                             @RequestParam("Details") String details,
                             @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        if (name.isEmpty()) {
            return "ERROR";
        } else {
            boolean airfoilIsAdd = service.addAirfoil(shortName, name, details, fileAirfoil, files);
            if (airfoilIsAdd) {
                return "airfoil Is Added";
            } else {
                return "Error";
            }
        }
    }

    @RequestMapping(value = "/updateAirfoil", method = RequestMethod.POST)
    public String updateAirfoil(@RequestParam("files") List<MultipartFile> files,
                                @RequestParam("name") String name,
                                @RequestParam("ShortName") String shortName,
                                @RequestParam("Details") String details,
                                @RequestParam("fileAirfoil") MultipartFile fileAirfoil) {
        if (name.isEmpty()) {
            return "ERROR";
        } else {
            boolean airfoilIsAdd = service.updateAirfoil(shortName, name, details, fileAirfoil, files);
            if (airfoilIsAdd) {
                return "airfoil Is Added";
            } else {
                return "Error";
            }
        }
    }


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {
        boolean parse = service.parse();
        if (parse) {
            return "OK";
        } else {
            return "ERROR";
        }

    }


}
