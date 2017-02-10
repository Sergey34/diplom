package net.sergey.diplom.services;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.UserDto;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.CoordinatesDto;
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

    public AirfoilDetail airfoilToAirfoilDetail(Airfoil airfoil) {
        AirfoilDetail airfoilDetail = new AirfoilDetail();
        airfoilDetail.setName(airfoil.getName());
        airfoilDetail.setDescription(airfoil.getDescription());
        airfoilDetail.setShortName(airfoil.getShortName());
        airfoilDetail.setImage("/files/airfoil_img/" + airfoil.getShortName() + ".png");

        airfoilDetail.setCoordView(airfoil.getCoordView());
        airfoilDetail.setCoordinates(coordinatesToCoordinatesDto(airfoil.getCoordinates()));

        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : ServiceImpl.CHART_NAMES) {
            imgCsvName.add("/files/chartTemp/" + airfoil.getShortName() + chartName + ".png");
        }
        airfoilDetail.setImgCsvName(imgCsvName);

        airfoilDetail.setStlFilePath("/files/scadFiles/" + airfoil.getShortName() + '_' + 100 + ".scad");
        return airfoilDetail;
    }

    public List<CoordinatesDto> coordinatesToCoordinatesDto(Set<Coordinates> coordinates) {
        List<CoordinatesDto> coordinatesDto = new ArrayList<>(coordinates.size());
        for (Coordinates coordinate : coordinates) {
            coordinatesDto.add(coordinatesToCoordinatesDto(coordinate));
        }
        return coordinatesDto;
    }

    public CoordinatesDto coordinatesToCoordinatesDto(Coordinates coordinate) {
        CoordinatesDto coordinatesDto = new CoordinatesDto();
        coordinatesDto.setFileName(coordinate.getFileName());
        coordinatesDto.setCoordinatesJson(coordinate.getCoordinatesJson());
        coordinatesDto.setMaxClCd(coordinate.getMaxClCd());
        coordinatesDto.setNCrit(coordinate.getNCrit());
        coordinatesDto.setRenolgs(coordinate.getRenolgs());
        coordinatesDto.setFilePath("/files/tmpCsv/" + coordinate.getFileName());
        return coordinatesDto;
    }

    public UserDto userToUserDto(User userByName, List roleByUsername) {
        UserDto userDto = new UserDto();
        userDto.setUserName(userByName.getUserName());
        userDto.setUserRoles(roleByUsername);
        return userDto;
    }
}