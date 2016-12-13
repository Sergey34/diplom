package net.sergey.diplom.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

    @RequestMapping("/")
    public String home() {
        return "redirect:/context";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "static/login.html";
    }

    @RequestMapping(value = "/loginError", method = RequestMethod.GET)
    public String loginError() {
        return "static/loginError.html";
    }

    @RequestMapping(value = "/context", method = RequestMethod.GET)
    public String showContext() {
        return "static/airfoilList.html";
    }

    @RequestMapping(value = "/adduser", method = RequestMethod.GET)
    public String showAddUser() {
        return "static/add_user.html";
    }

    @RequestMapping(value = "/adminka", method = RequestMethod.GET)
    public String showAdminka() {
        return "static/adminka.html";
    }

    @RequestMapping(value = "/load", method = RequestMethod.GET)
    public String showLoaderFile() {
        return "static/addAirfoil.html";
    }

    @RequestMapping(value = "/getDetailInfo", method = RequestMethod.GET)
    public String showDetailInfo() {
        return "static/detailInfo.html";
    }

    @RequestMapping(value = "/editAirfoil", method = RequestMethod.GET)
    public String showEditAirfoil() {
        return "static/edit_airfoil.html";
    }

    @RequestMapping(value = "/initDB", method = RequestMethod.GET)
    public String showInitDb() {
        return "static/initDB.html";
    }

}
