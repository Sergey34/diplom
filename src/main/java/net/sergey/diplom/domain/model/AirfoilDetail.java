package net.sergey.diplom.domain.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AirfoilDetail extends AirfoilDTO {
    private static final AirfoilDetail airfoilDetailError = new AirfoilDetail(true);
    private boolean error;
    private Set<Coordinates> fileCsvName;
    private List<String> imgCsvName;
    private String warnMessage;
    private List<SimilarAirfoil> similarAirfoilsId;
    private String stlFilePath;

    public AirfoilDetail(Airfoil airfoil, String message) {
        super(airfoil);
        fileCsvName = airfoil.getCoordinates();
        this.warnMessage = message;
    }

    private AirfoilDetail(boolean error) {
        this.error = error;
    }

    public AirfoilDetail(Airfoil airfoil, List<String> chartNames, String stlFilePath) {
        super(airfoil);
        fileCsvName = airfoil.getCoordinates();
        this.similarAirfoilsId = new ArrayList<>();
        for (Airfoil airfoilSimilar : airfoil.getAirfoilsSimilar()) {
            this.similarAirfoilsId.add(new SimilarAirfoil(airfoilSimilar.getId(), airfoilSimilar.getName()));

        }
        imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add("/resources/chartTemp/" + airfoil.getId() + chartName + ".png");
        }
        this.stlFilePath = "/resources/" + stlFilePath;
    }

    public static AirfoilDetail getAirfoilDetailError(String description) {
        airfoilDetailError.setDescription(description);
        return airfoilDetailError;
    }

    public Set<Coordinates> getFileCsvName() {
        return fileCsvName;
    }

    public void setFileCsvName(Set<Coordinates> fileCsvName) {
        this.fileCsvName = fileCsvName;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getWarnMessage() {
        return warnMessage;
    }

    public void setWarnMessage(String warnMessage) {
        this.warnMessage = warnMessage;
    }

    public List<String> getImgCsvName() {
        return imgCsvName;
    }

    public void setImgCsvName(List<String> imgCsvName) {
        this.imgCsvName = imgCsvName;
    }

    public List<SimilarAirfoil> getSimilarAirfoilsId() {
        return similarAirfoilsId;
    }

    public void setSimilarAirfoilsId(List<SimilarAirfoil> similarAirfoilsId) {
        this.similarAirfoilsId = similarAirfoilsId;
    }

    public String getStlFilePath() {
        return stlFilePath;
    }

    public void setStlFilePath(String stlFilePath) {
        this.stlFilePath = stlFilePath;
    }
}