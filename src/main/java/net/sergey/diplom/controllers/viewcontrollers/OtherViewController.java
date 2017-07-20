package net.sergey.diplom.controllers.viewcontrollers;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.dto.user.UserView;
import net.sergey.diplom.services.mainservice.ServiceAirfoil;
import net.sergey.diplom.services.parser.ParserServiceAirfoilTools;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static net.sergey.diplom.dto.messages.Message.SC_FORBIDDEN;

@Controller
public class OtherViewController {
    private final ServiceAirfoil serviceAirfoil;
    private final UserService userService;
    private final ParserServiceAirfoilTools parserService;

    @Autowired
    public OtherViewController(ServiceAirfoil serviceAirfoil, UserService userService, ParserServiceAirfoilTools parserService) {
        this.serviceAirfoil = serviceAirfoil;
        this.userService = userService;
        this.parserService = parserService;
    }

    @GetMapping("/about")
    public String about(Map<String, Object> model) {
        fillMandatoryData(model);
        return "about";
    }

    @GetMapping("/adminka")
    public String adminka(Map<String, Object> model) {
        fillMandatoryData(model);
        return "adminka";
    }

    @GetMapping("/add_user")
    public String addUser(Map<String, Object> model) {
        fillMandatoryData(model);
        List<String> roles = userService.getAllUserRoles();
        model.put("userView", new UserView());
        model.put("roles", roles);
        return "add_user";
    }

    @PostMapping("/add_user")
    public String addUser(Map<String, Object> model, @ModelAttribute UserView user) {
        User userResult = userService.addUser(user);
        fillMandatoryData(model);
        model.put("userView", new UserView());
        model.put("userResult", userResult != null);
        return "add_user";
    }


    @GetMapping("/login")
    public String login(Map<String, Object> model) {
        fillMandatoryData(model);
        return "login";
    }

    @GetMapping("/update_database")
    public String updateDatabase(Map<String, Object> model) {
        fillMandatoryData(model);
        return "update_database";
    }

    private void fillMandatoryData(Map<String, Object> model) {
        UserDto currentUser = userService.getCurrentUserInfo();
        List<Menu> menu = serviceAirfoil.getMenu();
        model.put("user", currentUser);
        model.put("menu", menu);
    }

    @ResponseBody
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public Future<Message> init() {
        if (parserService.parsingIsStarting()) {
            return new AsyncResult<>(new Message("В данный момент данные уже кем-то обновляются. Необходимо дождаться завершения обновления", SC_FORBIDDEN));
        }
        return parserService.startParsing();
    }

    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    public Message stop() {
        return parserService.stopParsing();
    }

    @ResponseBody
    @RequestMapping(value = "/clearAll", method = RequestMethod.GET)
    public Message clearAll() {
        return serviceAirfoil.clearAll();
    }

}
