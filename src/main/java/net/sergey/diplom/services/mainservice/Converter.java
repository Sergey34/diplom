package net.sergey.diplom.services.mainservice;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import net.sergey.diplom.domain.airfoil.Prefix;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.CharacteristicsDto;
import net.sergey.diplom.dto.user.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class Converter {

    public List<AirfoilDTO> airfoilToAirfoilDto(List<Airfoil> allAirfoils) {
        List<AirfoilDTO> airfoilsDTO = new ArrayList<>(allAirfoils.size());
        for (Airfoil airfoil : allAirfoils) {
            airfoilsDTO.add(airfoilToAirfoilDto(airfoil));
        }
        return airfoilsDTO;
    }

    public AirfoilDTO airfoilToAirfoilDto(Airfoil airfoil) {
        AirfoilDTO airfoilDTO = new AirfoilDTO();
        airfoilDTO.setName(airfoil.getName());
        airfoilDTO.setDescription(airfoil.getDescription());
        airfoilDTO.setShortName(airfoil.getShortName());
        airfoilDTO.setImage("/files/airfoil_img/" + airfoil.getShortName() + ".png");
        return airfoilDTO;
    }

    public AirfoilDetail airfoilToAirfoilDetail(Airfoil airfoil, List<String> chartNames, List<String> stlFileNames) {
        AirfoilDetail airfoilDetail = new AirfoilDetail();
        airfoilDetail.setName(airfoil.getName());
        airfoilDetail.setDescription(airfoil.getDescription());
        airfoilDetail.setShortName(airfoil.getShortName());
        airfoilDetail.setImage("/files/airfoil_img/" + airfoil.getShortName() + ".png");

        airfoilDetail.setCoordView(airfoil.getCoordView());
        airfoilDetail.setCharacteristics(coordinatesToCoordinatesDto(airfoil.getCharacteristics()));

        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add("/files/chartTemp/" + airfoil.getShortName() + chartName + ".png");
        }
        airfoilDetail.setImgCsvName(imgCsvName);

        airfoilDetail.setStlFilePath(new ArrayList<String>());
        if (stlFileNames != null) {
            for (String stlFileName : stlFileNames) {
                airfoilDetail.addStlFilePath("/files/scadFiles/" + stlFileName);
            }
        }

        return airfoilDetail;
    }

    public List<CharacteristicsDto> coordinatesToCoordinatesDto(Set<Characteristics> coordinates) {
        List<CharacteristicsDto> characteristicsDto = new ArrayList<>(coordinates.size());
        for (Characteristics coordinate : coordinates) {
            characteristicsDto.add(coordinatesToCoordinatesDto(coordinate));
        }
        return characteristicsDto;
    }

    public CharacteristicsDto coordinatesToCoordinatesDto(Characteristics coordinate) {
        CharacteristicsDto characteristicsDto = new CharacteristicsDto();
        characteristicsDto.setFileName(coordinate.getFileName());
        characteristicsDto.setCoordinatesJson(coordinate.getCoordinatesJson());
        characteristicsDto.setMaxClCd(coordinate.getMaxClCd());
        characteristicsDto.setNCrit(coordinate.getNCrit());
        characteristicsDto.setRenolgs(coordinate.getRenolgs());
        characteristicsDto.setFilePath("/files/tmpCsv/" + coordinate.getFileName());
        return characteristicsDto;
    }

    public UserDto userToUserDto(User userByName) {
        UserDto userDto = new UserDto();
        userDto.setUserName(userByName.getUserName());
        userDto.setUserRoles(userByName.getAuthorities());
        return userDto;
    }

    public MenuItem prefixToMenuItem(Prefix prefix) {
        return new MenuItem(String.valueOf(prefix.getPrefix()), String.valueOf(prefix.getPrefix()));
    }
}
