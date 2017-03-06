package net.sergey.diplom.services.usermanagerservice;

import net.sergey.diplom.dao.user.DaoAuthorities;
import net.sergey.diplom.dao.user.DaoUser;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.dto.user.UserView;
import net.sergey.diplom.services.mainservice.Converter;
import net.sergey.diplom.services.utils.UtilsLogger;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static net.sergey.diplom.dto.messages.Message.SC_CONFLICT;
import static net.sergey.diplom.dto.messages.Message.SC_OK;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final Converter converter;
    private final DaoUser daoUser;
    private final DaoAuthorities daoAuthorities;

    @Autowired
    public UserService(Converter converter, DaoUser daoUser, DaoAuthorities daoAuthorities) {
        this.converter = converter;
        this.daoUser = daoUser;
        this.daoAuthorities = daoAuthorities;
    }

    public Message addUser(UserView userView) {
        User user = new User();
        user.setEnabled(true);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        List<Authorities> authorities = new ArrayList<>();
        for (String role : userView.getRole()) {
            authorities.add(new Authorities(role, user.getUserName()));
        }
        try {
            daoUser.save(user);
            daoAuthorities.save(authorities);
            LOGGER.trace("Пользователь успешно создан");
            return new Message("Пользователь успешно создан", SC_OK);
        } catch (ConstraintViolationException e) {
            LOGGER.trace("пользователь с именем {} уже существует в базе.", user.getUserName());
            return new Message("Пользователь с таким именем уже существует, Выберите другое имя", SC_CONFLICT);
        } catch (Exception e) {
            LOGGER.trace("Ошибка при добавлении пользователя {}, {}", user.getUserName(), e);
            return new Message("Ошибка при добавлении пользователя", SC_CONFLICT);
        }
    }


    public List<Authorities> getAllUserRoles() {
        return daoAuthorities.findAll();
    }

    public UserDto getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isLogin = authentication.isAuthenticated();
        String name = authentication.getName();
        if (!"anonymousUser".equals(name) && isLogin) {
            return converter.userToUserDto(daoUser.findOneByUserName(name), daoAuthorities.findByUsername(name));
        }
        return null;
    }

    @PostConstruct
    public void init() {
        if (daoUser.findOneByUserName("admin") == null || daoAuthorities.findByUsername("admin").size() == 0) {
            User user = new User();
            user.setEnabled(true);
            user.setUserName("admin");
            String password = new BCryptPasswordEncoder().encode("mex_mat");
            user.setPassword(password);
            Authorities adminRole = new Authorities("ROLE_ADMIN", "admin");
            daoUser.save(user);
            daoAuthorities.save(adminRole);
        }
    }
}
