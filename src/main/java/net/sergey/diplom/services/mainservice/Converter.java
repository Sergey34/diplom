package net.sergey.diplom.services.mainservice;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Characteristics;
import net.sergey.diplom.domain.menu.MenuItem;
import net.sergey.diplom.domain.user.User;
import net.sergey.diplom.dto.airfoil.AirfoilDTO;
import net.sergey.diplom.dto.airfoil.AirfoilDetail;
import net.sergey.diplom.dto.airfoil.CharacteristicsDto;
import net.sergey.diplom.dto.user.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
@Component
public class Converter {
    @Value("${fileformat.source}")
    private String sourceFileFormat;
    @Value("${fileformat.img}")
    private String imgFileFormat;
    @Value("${dir.tmpCsv}")
    private String tmpCsvFilePath;
    @Value("${dir.scadFiles}")
    private String scadFilePath;
    @Value("${dir.chartTemp}")
    private String chartTempFilePath;
    @Value("${dir.airfoil_img}")
    private String airfoil_imgFilePath;

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
        airfoilDTO.setImage(airfoil_imgFilePath + airfoil.getShortName() + imgFileFormat);
        return airfoilDTO;
    }

    public AirfoilDetail airfoilToAirfoilDetail(Airfoil airfoil, List<String> chartNames, List<String> stlFileNames, String directory) {
        AirfoilDetail airfoilDetail = new AirfoilDetail();
        airfoilDetail.setName(airfoil.getName());
        airfoilDetail.setDescription(airfoil.getDescription());
        airfoilDetail.setShortName(airfoil.getShortName());
        airfoilDetail.setImage(airfoil_imgFilePath + airfoil.getShortName() + imgFileFormat);

        airfoilDetail.setCoordView(airfoil.getCoordView());
        airfoilDetail.setCoordViewPath(airfoil_imgFilePath + airfoil.getShortName() + sourceFileFormat);
        airfoilDetail.setCharacteristics(characteristicsToCharacteristicsDto(airfoil.getCharacteristics()));

        List<String> imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add(chartTempFilePath + directory + airfoil.getShortName() + chartName + imgFileFormat);
        }
        airfoilDetail.setImgCsvName(imgCsvName);

        airfoilDetail.setStlFilePath(new ArrayList<>());
        if (stlFileNames != null) {
            for (String stlFileName : stlFileNames) {
                airfoilDetail.addStlFilePath(scadFilePath + stlFileName);
            }
        }

        return airfoilDetail;
    }

    public List<CharacteristicsDto> characteristicsToCharacteristicsDto(Set<Characteristics> characteristics) {
        if (characteristics == null) {
            return new ArrayList<>();
        }
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
        characteristicsDto.setMaxClCd(coordinate.getMaxClCd());
        characteristicsDto.setAlpha(coordinate.getAlpha());
        characteristicsDto.setNCrit(coordinate.getNCrit());
        characteristicsDto.setRenolgs(coordinate.getRenolgs());
        characteristicsDto.setFilePath(tmpCsvFilePath + coordinate.getFileName());
        return characteristicsDto;
    }

    public UserDto userToUserDto(User userByName) {
        UserDto userDto = new UserDto();
        userDto.setUserName(userByName.getUserName());
        userDto.setUserRoles(userByName.getAuthorities());
        return userDto;
    }

    public MenuItem prefixToMenuItem(char prefix) {
        return MenuItem.builder().name(String.valueOf(prefix)).url(String.valueOf(prefix)).build();
    }
}
