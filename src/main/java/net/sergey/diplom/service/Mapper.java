package net.sergey.diplom.service;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.model.AirfoilAbstract;
import net.sergey.diplom.model.AirfoilId;

import java.util.ArrayList;
import java.util.List;

public class Mapper {
    public static List<AirfoilAbstract> mappAirfoilOnAirfoilLink(List<Airfoil> airfoils) {
        List<AirfoilAbstract> airfoilAbstracts = new ArrayList<>();
        for (Airfoil airfoil : airfoils) {
            airfoilAbstracts.add(new AirfoilId(airfoil));
        }
        return airfoilAbstracts;
    }
}
