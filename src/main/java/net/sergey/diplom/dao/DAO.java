package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.domain.user.User;

import java.util.List;

public interface DAO {

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count, boolean isLazyLoad);

    List<Menu> getAllMenu();

    void addUser(User user);

    User getUserByName(String name);

    void addMenus(List<Menu> menus);

    List<Authorities> getAllUserRoles();

    void addAirfoils(List<Airfoil> airfoils);

    void addAirfoil(Airfoil airfoil);

    Airfoil getAirfoilById(String id);

    int getCountAirfoilByPrefix(char prefix);

    MenuItem getMenuItemByUrl(String prefix);

    List<Airfoil> getAllAirfoils(int startNumber, int count, boolean isLasyLoad);
}