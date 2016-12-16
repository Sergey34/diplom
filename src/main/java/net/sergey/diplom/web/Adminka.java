package net.sergey.diplom.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class Adminka {
    @RequestMapping(value = "/adminka", method = RequestMethod.GET)
    public String showAdminka() {
        return "static/adminka/adminka.html";
    }

    @RequestMapping(value = "/adduser", method = RequestMethod.GET)
    public String showAddUser() {
        return "static/adminka/add_user.html";
    }

    @RequestMapping(value = "/addAirfoil", method = RequestMethod.GET)
    public String addAirfoil() {
        return "static/adminka/addAirfoil.html";
    }

    @RequestMapping(value = "/editAirfoil", method = RequestMethod.GET)
    public String showEditAirfoil() {
        return "static/adminka/edit_airfoil.html";
    }

    @RequestMapping(value = "/initDB", method = RequestMethod.GET)
    public String showInitDb() {
        return "static/adminka/initDB.html";
    }
}
