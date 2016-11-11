package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;

import java.util.List;

public interface DAO {

    List<Airfoil> getAirfoilsWithLinksByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getAirfoilsWithCoordinatesByPrefix(char prefix, int startNumber, int count);

    List<Menu> getAllMenu();

    void addUser(User user);

    List<User> getUserByName(String name);

    void addMenus(List<Menu> menus);

    List<UserRole> getAllUserRoles();

    void cleanAllTables();

    void addAirfoils(List<Airfoil> airfoils);

    void addAirfoils(Airfoil airfoil);

    int getIdLinkByUrl(String link);

    List getCoord();

    Airfoil getAirfoilById(int id);
}