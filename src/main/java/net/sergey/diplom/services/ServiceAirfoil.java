package net.sergey.diplom.services;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.AirfoilEdit;
import net.sergey.diplom.dto.messages.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceAirfoil {

    List<Menu> getMenu();

    List<AirfoilDTO> getAirfoilsDtoByPrefix(char prefix, int startNumber, int count);

    List<Airfoil> getAllAirfoils(int startNumber, int count);

    AirfoilDetail getDetailInfo(String airfoilId);

    Message addAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    int getCountAirfoilByPrefix(char prefix);

    List<String> updateGraf(String airfoilId, List<String> checkedList);

    Message updateAirfoil(String shortName, String name, String details, MultipartFile fileAirfoil, List<MultipartFile> files);

    Message addAirfoil(AirfoilEdit airfoilEdit);

    Message updateAirfoil(AirfoilEdit airfoilEdit);

    List<AirfoilDTO> getAllAirfoilDto(int startNumber, int count);

    List<Airfoil> getAirfoilsByPrefix(char prefix, int startNumber, int count);

    Airfoil getAirfoilById(String airfoilId);
}
