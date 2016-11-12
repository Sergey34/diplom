package net.sergey.diplom.model;

import net.sergey.diplom.domain.airfoil.Airfoil;

public class AirfoilDetail extends AirfoilAbstract {
    private static final AirfoilDetail airfoilDetailError = new AirfoilDetail(true);

    public AirfoilDetail(Airfoil airfoil) {
        super(airfoil);
    }

    private boolean error;

    private AirfoilDetail(boolean error) {
        this.error = error;
    }


    public static AirfoilDetail getAirfoilDetailError(String description) {
        airfoilDetailError.setDescription(description);
        return airfoilDetailError;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
