package net.sergey.diplom.controllers.viewcontrollers;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.services.mainservice.MenuService;
import net.sergey.diplom.services.usermanagerservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public abstract class AbstractController {
    protected final UserService userService;
    private final MenuService menuService;

    @Autowired
    protected AbstractController(MenuService menuService, UserService userService) {
        this.menuService = menuService;
        this.userService = userService;
    }

    protected void fillMandatoryData(Map<String, Object> model) {
        UserDto currentUser = userService.getCurrentUserInfo();
        List<Menu> menu = menuService.getMenu();
        model.put("user", currentUser);
        model.put("menu", menu);
    }
}
