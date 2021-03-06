package net.sergey.diplom.services.mainservice;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.dto.Condition;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceAirfoil {

    List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count);

    AirfoilDetail getDetailInfo(String airfoilId);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraph(String airfoilId, List<String> checkedList);

    Airfoil saveAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    Message saveAirfoil(AirfoilEdit airfoilEdit);

    Airfoil getAirfoilByShortName(String airfoilId);

    Message clearAll();

    List<AirfoilDTO> searchAirfoils(String name, int startNumber, int count);

    List<AirfoilDTO> findByShortNameLike(String shortName, int startNumber, int count);

    int countByShortNameLike(String shortName);

    int countSearchAirfoil(String shortName);

    List<AirfoilDTO> searchAirfoils(List<Condition> conditions, String template);
}
