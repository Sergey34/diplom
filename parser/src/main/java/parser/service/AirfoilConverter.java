package parser.service;

import base.domain.airfoil.Airfoil;
import base.dto.AirfoilDto;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AirfoilConverter {
    public AirfoilDto toDtoAirfoil(@NonNull Airfoil airfoil) {
        AirfoilDto airfoilDto = new AirfoilDto();
        airfoilDto.setName(airfoil.getName());
        airfoilDto.setShortName(airfoil.getShortName());
        airfoilDto.setDescription(airfoil.getDescription());
        airfoilDto.setPrefix(airfoil.getPrefix());
        airfoilDto.setShortName(airfoil.getCoordView());
        return airfoilDto;
    }
}
