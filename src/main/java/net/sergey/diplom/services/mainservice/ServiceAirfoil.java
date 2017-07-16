package net.sergey.diplom.services.mainservice;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceAirfoil {

    List<Menu> getMenu();

    List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count);

    AirfoilDetail getDetailInfo(String airfoilId);

    Airfoil addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraph(String airfoilId, List<String> checkedList);

    Airfoil updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    Message saveAirfoil(AirfoilEdit airfoilEdit);

    Airfoil getAirfoilByShortName(String airfoilId);

    Message clearAll();

    List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String name, int startNumber, int count);

    List<AirfoilDTO> findByShortNameLike(String shortName, int startNumber, int count);

    int countByShortNameLike(String shortName);

    int countSearchAirfoil(List<Condition> conditions, String shortName);
}
