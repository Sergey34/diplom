package net.sergey.diplom.services;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.user.Authorities;
import net.sergey.diplom.dto.UserDto;
import net.sergey.diplom.dto.UserView;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Future;

public interface ServiceInt {

    List<Menu> getMenu();

    Message addUser(UserView user);

    List<Authorities> getAllUserRoles();

    List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getAllAirfoils(int startNumber, int count);

    AirfoilDetail getDetailInfo(String airfoilId);

    Future<Message> parse();

    UserDto getCurrentUserInfo();

    Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraf(String airfoilId, List<String> checkedList);

    Message updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    Message addAirfoil(AirfoilEdit airfoilEdit);

    Message stop();

    boolean parsingIsStarting();

    Message updateAirfoil(AirfoilEdit airfoilEdit);

    List<AirfoilDTO> getAllAirfoilDto(int startNumber, int count);

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    Airfoil getAirfoilById(String airfoilId);
}
