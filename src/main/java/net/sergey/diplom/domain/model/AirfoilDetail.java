package net.sergey.diplom.domain.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Coordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AirfoilDetail extends AirfoilDTO {
    private Set<Coordinates> fileCsvName;
    private List<String> imgCsvName;

    private String stlFilePath;

    public AirfoilDetail(Airfoil airfoil, List<String> chartNames, String stlFilePath) {
        super(airfoil);
        fileCsvName = airfoil.getCoordinates();


        imgCsvName = new ArrayList<>();
        for (String chartName : chartNames) {
            imgCsvName.add("/resources/chartTemp/" + airfoil.getId() + chartName + ".png");
        }
        this.stlFilePath = "/resources/" + stlFilePath;
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



    public String getStlFilePath() {
        return stlFilePath;
    }

    public void setStlFilePath(String stlFilePath) {
        this.stlFilePath = stlFilePath;
    }
}
