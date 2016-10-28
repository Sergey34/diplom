package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;

import java.util.ArrayList;
import java.util.List;

public interface DAO {

    List<Profile> getProfilesByPrefix(char prefix);

    List<Profile> getProfilesByName(String name);

    List<Profile> getAllProfiles();

    boolean addProfile(Profile profile);

    Profile updateProfile(Profile profile);

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
}