package net.sergey.diplom.rest;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilAbstract;
import net.sergey.diplom.service.ServiceInt;
import net.sergey.diplom.service.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ServiceInt service;

    @Autowired
    public FullRestController(ServiceInt service) {
        this.service = service;
    }


    @RequestMapping(value = "/greeting", method = RequestMethod.GET)
    public String greeting() throws IOException, InterruptedException {
        LOGGER.info("!!!тестовый метод!!! запуск python скрипта");
        String args = "aargs";
        Process p = Runtime.getRuntime().exec("python3 /resources/myscript.py " + args);
        int i = p.waitFor();
        LOGGER.info(String.valueOf(i));

        File file = new File("/resources/" + args + ".txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String s = bufferedReader.readLine();
        LOGGER.info(s);


        return "";
    }

    @RequestMapping(value = "/aaa", method = RequestMethod.GET)
    public List<Menu> getMaa() throws IOException {
        LOGGER.info("ifoLogger {} ", "wqe");
        LOGGER.debug("ifoLogger");
        LOGGER.warn("ifoLogger");
        LOGGER.error("ifoLogger");
        return service.getMenu();
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


    @RequestMapping(value = "/userRoles", method = RequestMethod.GET)
    public List<UserRole> getUserRole() throws IOException {
        return service.getAllUserRoles();
    }

    @RequestMapping(value = "/getContext/{prefix}/{startNumber}/{count}", method = RequestMethod.GET)
    public List<AirfoilAbstract> getAirfoilByPrefix(@PathVariable char prefix, @PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAirfoilsByPrefix(prefix, startNumber, count);
    }

    @RequestMapping(value = "/getContext/allAirfoil/{startNumber}/{count}", method = RequestMethod.GET)
    public List<Airfoil> getAirfoilByPrefix(@PathVariable int startNumber, @PathVariable int count) throws IOException {
        return service.getAllAirfoils(startNumber, count);
    }



}
