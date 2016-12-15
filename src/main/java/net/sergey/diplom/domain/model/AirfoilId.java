package net.sergey.diplom.domain.model;

import net.sergey.diplom.domain.airfoil.Airfoil;

public class AirfoilId extends AirfoilDTO {
    private int id;

    public AirfoilId(Airfoil airfoil) {
        super(airfoil);
        this.id = airfoil.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
