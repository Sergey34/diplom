package net.sergey.diplom.rest;

import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.model.UserView;
import net.sergey.diplom.service.ServiceInt;
import net.sergey.diplom.service.utils.UtilRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/rest/write")
@RestController(value = "write")
public class RestWriteController {
    @Autowired
    ServiceInt service;

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


}
