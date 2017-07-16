package net.sergey.diplom.services.usermanagerservice;

import lombok.extern.slf4j.Slf4j;
import net.sergey.diplom.dao.user.DaoUser;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.user.UserDto;
import net.sergey.diplom.dto.user.UserView;
import net.sergey.diplom.services.mainservice.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserService {
    private final static List<String> allAuthorities;

    static {
        allAuthorities = Collections.singletonList("ROLE_ADMIN");
    }

    private final Converter converter;
    private final DaoUser daoUser;


    @Autowired
    public UserService(Converter converter, DaoUser daoUser) {
        this.converter = converter;
        this.daoUser = daoUser;
    }

    public List<String> getAllUserRoles() {
        return allAuthorities;
    }


    public User addUser(UserView userView) {
        User user = User.builder()
                .enabled(true)
                .password(new BCryptPasswordEncoder().encode(userView.getPassword()))
                .userName(userView.getName())
                .authorities(userView.getRoles().stream().map(s -> new Authorities(s, userView.getName())).collect(Collectors.toList()))
                .build();

        if (daoUser.findOneByUserName(userView.getName()) != null) {
            log.trace("Ошибка при добавлении пользователя {}", userView.getName());
            return user;
        }
        try {
            daoUser.save(user);
            log.trace("Пользователь успешно создан");
            return user;
        } catch (Exception e) {
            log.trace("Ошибка при добавлении пользователя {}", user.getUserName(), e);
            return user;
        }
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
            Authorities adminRole = new Authorities("ROLE_ADMIN", "ROLE_ADMIN");
            user.setAuthorities(Collections.singletonList(adminRole));
            daoUser.save(user);
        }
    }
}
