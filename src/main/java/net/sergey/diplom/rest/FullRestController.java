package net.sergey.diplom.rest;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Service;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/rest")
@RestController
public class FullRestController {
    private final Service service;
    private static final Logger logger = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());

    @Autowired
    public FullRestController(Service service) {
        this.service = service;
    }


    int i = 0;

    @RequestMapping(value = "/geta", method = RequestMethod.GET)
    public Settings getA() {
        logger.info("getA() start");
        Settings settings = new Settings();
        settings.setFriday(false);
        settings.setGroup("123" + i);
        i++;
        return settings;
    }

    @RequestMapping(value = "/admin/greeting", method = RequestMethod.GET)
    public Map<String, Integer> greeting() throws IOException, InterruptedException {
        logger.info("#####jgh");
        String args = "aargs";
        Process p = Runtime.getRuntime().exec("python3 /home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/myscript.py " + args);
        int i = p.waitFor();
        System.out.println(i);

        File file = new File("/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/" + args + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();
        System.out.println(s);

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put("qwe1", 123);
        stringIntegerHashMap.put("qwe2", 1232);
        stringIntegerHashMap.put("qwe3", 1233);
        stringIntegerHashMap.put("qwe4", 1235);
        return stringIntegerHashMap;
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public List<Menu> getMenu() throws IOException {
        List<Menu> menu = service.getMenu();
        return menu;
    }

    private String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    public String getUserInfo() throws IOException {
        Boolean isLogin = SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        if (!"guest".equals(getCurrentUserName()) && isLogin) {
            return getCurrentUserName();
        }
        return null;
    }
}
