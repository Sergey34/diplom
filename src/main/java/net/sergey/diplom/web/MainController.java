package net.sergey.diplom.web;

import com.google.gson.Gson;
import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;
import net.sergey.diplom.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {
    private static final String SSU_PARSER = "net.schastny.contactmanager.service.Parsers.SSU_Table.faculty.Creator";

    @Autowired
    private Service service;

    @RequestMapping("/index")
    public ModelAndView showData() {
        String data = service.showData(getCurrentUserName());
        ModelAndView model = new ModelAndView("context");
        model.addObject("info", data);
        return model;
    }

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
        String atribJson = setting.toString();
        Filter filter = new Filter();
        filter.setAtribJson(atribJson);
        filter.setUsername(getCurrentUserName());
        service.saveSettings(filter);
        return "redirect:/index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public Object login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {

        Boolean isLogin = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        if (!"guest".equals(getCurrentUserName()) && isLogin) {
            return "redirect:/index";
        }
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }
        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login");
        return model;
    }
}
