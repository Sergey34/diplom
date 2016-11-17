package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.model.AirfoilDTO;
import net.sergey.diplom.model.AirfoilId;

import java.util.ArrayList;
import java.util.List;

public class Mapper {
    public static List<AirfoilDTO> mapAirfoilOnAirfoilId(List<Airfoil> airfoils) {
        List<AirfoilDTO> airfoilDTOs = new ArrayList<>();
        for (Airfoil airfoil : airfoils) {
            airfoilDTOs.add(new AirfoilId(airfoil));
        }
        return airfoilDTOs;
    }
}
