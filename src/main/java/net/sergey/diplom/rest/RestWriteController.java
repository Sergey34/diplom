package net.sergey.diplom.rest;

import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.model.UserView;
import net.sergey.diplom.service.ServiceInt;
import net.sergey.diplom.service.parser.Parser;
import net.sergey.diplom.service.utils.UtilRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RequestMapping(value = "/rest/write")
@RestController(value = "write")
public class RestWriteController {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ServiceInt service;

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String addUser(@RequestBody UserView userView) {
        User user = new User();
        user.setEnabled(1);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        user.setUserRoles(UtilRoles.findUserRoleByName(userView.getRole()));
        if (service.addUser(user)) {
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

        String path = servletContext.getRealPath("/resources/airfoil_img/");
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(path + name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";
            } catch (Exception e) {
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
        }
    }

    @Autowired
    private Parser parser;

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init() {
        try {
            parser.init();
            return "OK";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
