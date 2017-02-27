package net.sergey.diplom.dto.airfoil;

import java.util.List;

public class AirfoilDetail extends AirfoilDTO {
    private List<String> imgCsvName;
    private List<String> stlFilePath;
    private List<CoordinatesDto> coordinates;
    private String coordView;

    public AirfoilDetail() {

    }

    public List<String> getImgCsvName() {
        return imgCsvName;
    }

    public void setImgCsvName(List<String> imgCsvName) {
        this.imgCsvName = imgCsvName;
    }

    public List<CoordinatesDto> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<CoordinatesDto> coordinates) {
        this.coordinates = coordinates;
    }

    public String getCoordView() {
        return coordView;
    }

    public void setCoordView(String coordView) {
        this.coordView = coordView;
    }

    public List<String> getStlFilePath() {
        return stlFilePath;
    }

    public void setStlFilePath(List<String> stlFilePath) {
        this.stlFilePath = stlFilePath;
    }

    public void addStlFilePath(String filePath) {
        stlFilePath.add(filePath);
    }
}
