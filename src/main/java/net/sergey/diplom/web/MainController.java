package net.sergey.diplom.web;

import net.sergey.diplom.domain.User;
import net.sergey.diplom.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MainController {

    private final Service service;

    @Autowired
    public MainController(Service service) {
        this.service = service;
    }

    @RequestMapping("/ii")
    public String showHTML() {
        List<User> users = service.getUser("alex");
        System.out.println(users);

        System.out.println("start showHTML()");
        return "static/list.html";
    }

    @RequestMapping("/")
    public String home() {
        return "redirect:/login";
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
        return "static/context.html";
    }

}
