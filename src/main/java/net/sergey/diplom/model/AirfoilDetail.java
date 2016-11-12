package net.sergey.diplom.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class AirfoilDetail extends AirfoilAbstract {
    private static final AirfoilDetail airfoilDetailError = new AirfoilDetail(true);
    private boolean error;
    private List<String> fileCsvName;

    public AirfoilDetail(Airfoil airfoil) {
        super(airfoil);
        fileCsvName = new ArrayList<>();
        for (Coordinates coordinates : airfoil.getCoordinates()) {
            fileCsvName.add(coordinates.getFileName());
        }
    }

    private AirfoilDetail(boolean error) {
        this.error = error;
    }

    public static AirfoilDetail getAirfoilDetailError() {
        return airfoilDetailError;
    }

    public static AirfoilDetail getAirfoilDetailError(String description) {
        airfoilDetailError.setDescription(description);
        return airfoilDetailError;
    }

    public List<String> getFileCsvName() {
        return fileCsvName;
    }

    public void setFileCsvName(List<String> fileCsvName) {
        this.fileCsvName = fileCsvName;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
