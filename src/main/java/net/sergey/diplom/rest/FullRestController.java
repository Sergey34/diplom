package net.sergey.diplom.rest;

import net.sergey.diplom.domain.Menu;
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
import java.util.List;

@RequestMapping(value = "/rest")
@RestController
public class FullRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final Service service;

    @Autowired
    public FullRestController(Service service) {
        this.service = service;
    }


    @RequestMapping(value = "/admin/greeting", method = RequestMethod.GET)
    public String greeting() throws IOException, InterruptedException {
        LOGGER.info("!!!тестовый метод!!! запуск python скрипта");
        String args = "aargs";
        Process p = Runtime.getRuntime().exec("python3 /home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/myscript.py " + args);
        int i = p.waitFor();
        LOGGER.info(String.valueOf(i));

        File file = new File("/home/sergey/workspace/IdeaProjects/diplom/diplom/src/main/resources/" + args + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();
        LOGGER.info(s);


        return "";
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public List<Menu> getMenu() throws IOException {
        return service.getMenu();
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
