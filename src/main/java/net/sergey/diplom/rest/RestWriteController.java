package net.sergey.diplom.rest;

import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.model.UserView;
import net.sergey.diplom.service.ServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @RequestMapping(value = "/editAirfoil", method = RequestMethod.POST)
    public String editAirfoil(@RequestBody AirfoilView airfoilView) {
        if (service.updateAirfoil(airfoilView)) {
            return "{\"msg\":\"success\"}";
        }
        return "error";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("name") String name,
                                   @RequestParam("file") MultipartFile file) {


        if (!file.isEmpty()) {
            return service.fileUpload(name, file);
        } else {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }


    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {
        try {
            service.parse();
            return "OK";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
