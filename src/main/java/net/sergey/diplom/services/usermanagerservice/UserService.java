package net.sergey.diplom.services.usermanagerservice;

import net.sergey.diplom.dao.DAO;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.UserDto;
import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.messages.Message;
import net.sergey.diplom.services.Converter;
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
import java.util.List;

import static net.sergey.diplom.dto.messages.Message.SC_CONFLICT;
import static net.sergey.diplom.dto.messages.Message.SC_OK;

@Component
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    @Autowired
    private DAO dao;
    @Autowired
    private Converter converter;


    public Message addUser(UserView userView) {
        User user = new User();
        user.setEnabled(true);
        user.setPassword(userView.getPassword());
        user.setUserName(userView.getName());
        try {
            dao.addUser(user);
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
        return dao.getAllUserRoles();
    }

    public UserDto getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isLogin = authentication.isAuthenticated();
        String name = authentication.getName();
        if (!"anonymousUser".equals(name) && isLogin) {
            return converter.userToUserDto(dao.getUserByName(name), dao.getRoleByUsername(name));
        }
        return null;
    }

    @PostConstruct
    public void init() {
        if (dao.getUserByName("admin") == null || dao.getRoleByUsername("admin").size() == 0) {
            User user = new User();
            user.setEnabled(true);
            user.setUserName("admin");
            String password = new BCryptPasswordEncoder().encode("mex_mat");
            user.setPassword(password);
            Authorities adminRole = new Authorities("ROLE_ADMIN", "admin");
            dao.addUser(user);
            dao.addAuthority(adminRole);
        }
    }
}
