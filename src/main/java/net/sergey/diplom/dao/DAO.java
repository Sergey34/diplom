package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;

import java.util.List;
import java.util.Set;

public interface DAO {

    Set<Profile> getProfilesByPrefix(char prefix);

    Set<Profile> getProfilesByName(String name);

    Set<Profile> getAllProfiles();

    boolean addProfile(Profile profile);

    Profile updateProfile(Profile profile);

    boolean deleteProfileById(int id);

    boolean deleteProfileByName(String name);

    boolean deleteProfileByPrefix(char prefix);

    Set<Menu> getAllMenu();

    Set<Menu> getMenuByHeader(String header);

    boolean addMenu(Menu menu);

    Menu updateMenu(Menu menu);

    boolean addUser(User user);

    void updateUserPasword(String password);

    List<User> getUserById(String name);

}