package net.sergey.diplom.domain.airfoil;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Coordinates {
    @Id
    private int id;
    private String coordinatesJson;
    private String fileName;
    private String renolgs;
    private String nCrit;
    private String maxClCd;
    private String alpha;


    public Coordinates(String coordinatesJson, String fileNameId) {
        this.coordinatesJson = coordinatesJson;
        this.fileName = fileNameId;
        this.id = fileNameId.hashCode();
    }

    public Coordinates() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoordinatesJson() {
        return coordinatesJson;
    }

    public void setCoordinatesJson(String coordinatesJson) {
        this.coordinatesJson = coordinatesJson;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRenolgs() {
        return renolgs;
    }

    public void setRenolgs(String renolgs) {
        this.renolgs = renolgs;
    }

    public String getMaxClCd() {
        return maxClCd;
    }

    public void setMaxClCd(String maxClCd) {
        this.maxClCd = maxClCd;
    }

    public String getNCrit() {
        return nCrit;
    }

    public void setNCrit(String NCrit) {
        this.nCrit = NCrit;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }
}
