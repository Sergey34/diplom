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

@SuppressWarnings("WeakerAccess")
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

    public AirfoilDetail airfoilToAirfoilDetail(Airfoil airfoil, List<String> chartNames, List<String> stlFileNames, String directory) {
        AirfoilDetail airfoilDetail = new AirfoilDetail();
        airfoilDetail.setName(airfoil.getName());
        airfoilDetail.setDescription(airfoil.getDescription());
        airfoilDetail.setShortName(airfoil.getShortName());
        airfoilDetail.setImage("/files/airfoil_img/" + airfoil.getShortName() + ".png");

        airfoilDetail.setCoordView(airfoil.getCoordView());
        airfoilDetail.setCharacteristics(characteristicsToCharacteristicsDto(airfoil.getCharacteristics()));

        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add("/files/chartTemp/" + directory + airfoil.getShortName() + chartName + ".png");
        }
        airfoilDetail.setImgCsvName(imgCsvName);

        airfoilDetail.setStlFilePath(new ArrayList<>());
        if (stlFileNames != null) {
            for (String stlFileName : stlFileNames) {
                airfoilDetail.addStlFilePath("/files/scadFiles/" + stlFileName);
            }
        }

        return airfoilDetail;
    }

    public List<CharacteristicsDto> characteristicsToCharacteristicsDto(Set<Characteristics> characteristics) {
        List<CharacteristicsDto> characteristicsDto = new ArrayList<>(characteristics.size());
        for (Characteristics coordinate : characteristics) {
            characteristicsDto.add(characteristicsToCharacteristicsDto(coordinate));
        }
        return characteristicsDto;
    }

    public CharacteristicsDto characteristicsToCharacteristicsDto(Characteristics coordinate) {
        CharacteristicsDto characteristicsDto = new CharacteristicsDto();
        characteristicsDto.setFileName(coordinate.getFileName());
        characteristicsDto.setCoordinatesStl(coordinate.getCoordinatesStl());
        characteristicsDto.setMaxClCd(coordinate.getMaxClCd() + " " + coordinate.getAlpha());
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
        return MenuItem.builder().name(String.valueOf(prefix.getPrefix())).url(String.valueOf(prefix.getPrefix())).build();
    }
}
