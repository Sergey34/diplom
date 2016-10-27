package net.sergey.diplom.web;

import com.google.gson.Gson;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import net.sergey.diplom.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    private static final String SSU_PARSER = "net.schastny.contactmanager.service.Parsers.SSU_Table.faculty.Creator";

    private final Service service;

    @Autowired
    public MainController(Service service) {
        this.service = service;
    }

    @RequestMapping("/index")
    public ModelAndView showData() {
        String data = service.showData(getCurrentUserName());
        ModelAndView model = new ModelAndView("context");
        model.addObject("info", data);
        return model;
    }


    @RequestMapping("/ii")
    public String showHTML() {
        List<User> users = service.getUser("alex");
        System.out.println(users);

        System.out.println("start showHTML()");
        return "static/list.html";
    }

    /*@RequestMapping(value = "/login", method = {RequestMethod.POST, RequestMethod.GET})
    public String login() {
        System.out.println("Login");
        return "static/login.html";
    }
*/

    private String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @RequestMapping("/setting")
    public Map<String, Object> settings(Map<String, Object> map) throws IOException {
        Settings SettingLoad =
                service.loadSetting(getCurrentUserName());
        map.put("setting", SettingLoad);
        //выпадающий список

        Parser parser = service.getBeanParserByName(SSU_PARSER);

        String facultyStr = parser.getData("");
        Map groupMap = jsonToObject(facultyStr);
        map.put("groupList", groupMap);
        return map;
    }

    private HashMap jsonToObject(String jsonText) {
        return new Gson().fromJson(jsonText, HashMap.class);
    }


    @RequestMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveSettings(@ModelAttribute("setting") Settings setting) {
//        String atribJson = setting.toString();
//        Filter filter = new Filter();
//        filter.setAtribJson(atribJson);
//        filter.setUsername(getCurrentUserName());
//        service.saveSettings(filter);
        return "redirect:/index";
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
