package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.model.AirfoilDTO;
import net.sergey.diplom.domain.model.AirfoilDetail;
import net.sergey.diplom.domain.model.AirfoilEdit;
import net.sergey.diplom.domain.model.UserView;
import net.sergey.diplom.domain.model.messages.Message;
import net.sergey.diplom.domain.user.UserRole;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

public interface ServiceInt {

    List<Menu> getMenu();

    Message addUser(UserView user);

    List<UserRole> getAllUserRoles();

    List<AirfoilDTO> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getAllAirfoils(int startNumber, int count);

    AirfoilDetail getDetailInfo(int airfoilId);

    Future<Message> parse();

    String getCurrentUserInfo();

    Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraf(int airfoilId, List<String> checkedList);

    Message updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    Message addAirfoil(AirfoilEdit airfoilEdit);

    Message stop();

    boolean parsingIsStarting();

    Message updateAirfoil(AirfoilEdit airfoilEdit);
}
