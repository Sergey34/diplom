package net.sergey.diplom.services.usermanagerservice;

import net.sergey.diplom.dao.user.DaoUser;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.dto.user.UserView;
import net.sergey.diplom.services.mainservice.Converter;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

import static net.sergey.diplom.dto.messages.Message.SC_CONFLICT;
import static net.sergey.diplom.dto.messages.Message.SC_OK;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final static List<Authorities> allAuthorities;

    static {
        allAuthorities = Arrays.asList(new Authorities("", "ROLE_ADMIN"));
    }

    private final Converter converter;
    private final DaoUser daoUser;


    @Autowired
    public UserService(Converter converter, DaoUser daoUser) {
        this.converter = converter;
        this.daoUser = daoUser;
    }

    public Message addUser(UserView userView) {
        User user = new User();
        user.setEnabled(true);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        try {
            daoUser.save(user);
            LOGGER.trace("Пользователь успешно создан");
            return new Message("Пользователь успешно создан", SC_OK);
        } catch (Exception e) {
            LOGGER.trace("Ошибка при добавлении пользователя {}, {}", user.getUserName(), e);
            return new Message("Ошибка при добавлении пользователя", SC_CONFLICT);
        }
    }



    public List<Authorities> getAllUserRoles() {
        return allAuthorities;
    }

    public UserDto getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isLogin = authentication.isAuthenticated();
        String name = authentication.getName();
        if (!"anonymousUser".equals(name) && isLogin) {
            return converter.userToUserDto(daoUser.findOneByUserName(name));
        }
        return null;
    }

    @PostConstruct
    public void init() {
        if (daoUser.findOneByUserName("admin") == null) {
            User user = new User();
            user.setEnabled(true);
            user.setUserName("admin");
            String password = new BCryptPasswordEncoder().encode("mex_mat");
            user.setPassword(password);
            Authorities adminRole = new Authorities("ROLE_ADMIN", "admin");
            user.setAuthorities(Arrays.asList(adminRole));
            daoUser.save(user);
        }
    }
}
