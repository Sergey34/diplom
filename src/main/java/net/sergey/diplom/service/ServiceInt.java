package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilAbstract;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.model.AirfoilView;
import net.sergey.diplom.model.UserView;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServiceInt {

    List<Menu> getMenu() throws IOException;

    List<User> getUser(String alex);

    boolean isValidUser(String name);

    boolean addUser(UserView user);

    List<UserRole> getAllUserRoles();

    void init();

    void clean();

    List<AirfoilAbstract> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    boolean updateAirfoil(AirfoilView airfoilView);

    List<Airfoil> getAllAirfoils(int startNumber, int count);

    AirfoilDetail getDetailInfo(int airfoilId);

    String fileUpload(String name, MultipartFile bytes);

    void parse() throws IOException;
}
