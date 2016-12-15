package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.domain.user.UserRole;
import net.sergey.diplom.model.AirfoilDTO;
import net.sergey.diplom.model.AirfoilDetail;
import net.sergey.diplom.model.UserView;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceInt {

    List<Menu> getMenu();

    List<User> getUser(String alex);

    boolean addUser(UserView user);

    List<UserRole> getAllUserRoles();



    List<AirfoilDTO> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getAllAirfoils(int startNumber, int count);

    AirfoilDetail getDetailInfo(int airfoilId);

    boolean parse();

    String getUserInfo();

    boolean createNewAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraf(int airfoilId, List<String> checkedList);
}
