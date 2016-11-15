package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;

import java.util.List;

public interface DAO {

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    List<Menu> getAllMenu();

    void addUser(User user);

    List<User> getUserByName(String name);

    void addMenus(List<Menu> menus);

    List<UserRole> getAllUserRoles();

    void cleanAllTables();

    void addAirfoil(List<Airfoil> airfoils);

    void addAirfoil(Airfoil airfoil);

    int getIdLinkByUrl(String link);

    Airfoil getAirfoilById(int id);

    List<Airfoil> getAllAirfoil();


}