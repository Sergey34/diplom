package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;

import java.io.IOException;
import java.util.List;

public interface ServiceInt {

    List<Menu> getMenu() throws IOException;

    List<User> getUser(String alex);

    boolean isValidUser(String name);

    boolean addUser(User user);

    List<UserRole> getAllUserRoles();

    public void init();

    void clean();

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count);
}
