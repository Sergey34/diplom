package net.sergey.diplom.dto.airfoil;

import java.util.List;

public class AirfoilEdit {
    private String airfoilName;
    private String shortName;
    private String details;
    private String viewCsv;
    private List<Data> data;

    public String getViewCsv() {
        return viewCsv;
    }

    public void setViewCsv(String viewCsv) {
        this.viewCsv = viewCsv;
    }

    public String getAirfoilName() {
        return airfoilName;
    }

    public void setAirfoilName(String airfoilName) {
        this.airfoilName = airfoilName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


}
