package net.sergey.diplom.service;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.domain.UserRole;

import java.io.IOException;
import java.util.List;

public interface Service {

    List<Menu> getMenu() throws IOException;

    Object getAirfoilsByLiteral(String literal) throws IOException;

    List<User> getUser(String alex);

    boolean isValidUser(String name);

    void addUser(User user);

    List<UserRole> getAllUserRoles();

    public void init();
}
