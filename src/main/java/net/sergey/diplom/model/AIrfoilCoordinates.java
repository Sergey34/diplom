package net.sergey.diplom.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;

import java.util.Set;

public class AIrfoilCoordinates extends AirfoilAbstract {
    private Set<Coordinates> coordinates;

    public AIrfoilCoordinates(Airfoil airfoil) {
        super(airfoil);
        this.coordinates = airfoil.getCoordinates();
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Set<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }
}
