package net.sergey.diplom.dao;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;

import java.util.ArrayList;
import java.util.List;

public interface DAO {

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getProfilesByName(String name);

    List<Airfoil> getAllProfiles();

    boolean addProfile(Airfoil profile);

    Airfoil updateProfile(Airfoil profile);

    boolean deleteProfileById(int id);

    boolean deleteProfileByName(String name);

    boolean deleteProfileByPrefix(char prefix);

    List<Menu> getAllMenu();

    List<Menu> getMenuByHeader(String header);

    void addMenu(Menu menu);

    Menu updateMenu(Menu menu);

    void addUser(User user);

    void updateUserPassword(String password);

    List<User> getUserByName(String name);

    void addMenus(ArrayList<Menu> menus);

    List<UserRole> getAllUserRoles();

    void cleanAllTables();

    void addAirfoils(List<Airfoil> airfoils);

    /*void addPrefix(Prefix prefix1);

    Prefix getPrefix(char prefix);*/
}