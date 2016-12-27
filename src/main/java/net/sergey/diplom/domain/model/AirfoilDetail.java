package net.sergey.diplom.domain.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;
import net.sergey.diplom.service.ServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AirfoilDetail extends AirfoilDTO {
    private Set<Coordinates> fileCsvName;
    private List<String> imgCsvName;
    private String stlFilePath;
    private Set<Coordinates> coordinates;
    private String coordView;


    public AirfoilDetail(Airfoil airfoil, List<String> chartNames, String stlFilePath) {
        super(airfoil);
        fileCsvName = airfoil.getCoordinates();
        coordView = airfoil.getCoordView();
        coordinates = airfoil.getCoordinates();
        imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add(ServiceImpl.getRootUrl() + "/resources/chartTemp/" + airfoil.getId() + chartName + ".png");
        }
        this.stlFilePath = ServiceImpl.getRootUrl() + "/resources/" + stlFilePath;
    }

    public Set<Coordinates> getFileCsvName() {
        return fileCsvName;
    }

    public void setFileCsvName(Set<Coordinates> fileCsvName) {
        this.fileCsvName = fileCsvName;
    }

    public List<String> getImgCsvName() {
        return imgCsvName;
    }

    public void setImgCsvName(List<String> imgCsvName) {
        this.imgCsvName = imgCsvName;
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Set<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public String getCoordView() {
        return coordView;
    }

    public void setCoordView(String coordView) {
        this.coordView = coordView;
    }

    public String getStlFilePath() {
        return stlFilePath;
    }

    public void setStlFilePath(String stlFilePath) {
        this.stlFilePath = stlFilePath;
    }
}
