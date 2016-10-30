package net.sergey.diplom.service;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;

import java.io.IOException;
import java.util.List;

public interface Service {

    List<Menu> getMenu() throws IOException;

    Object getAirfoilsByPrefix(String literal) throws IOException;

    List<User> getUser(String alex);

    boolean isValidUser(String name);

    boolean addUser(User user);

    List<UserRole> getAllUserRoles();

    public void init();

    void clean();
}
