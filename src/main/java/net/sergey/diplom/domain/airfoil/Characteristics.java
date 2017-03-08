package net.sergey.diplom.domain.airfoil;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Characteristics {
    @Id
    private int id;
    private String coordinatesStl;
    private String fileName;
    private double renolgs;
    private double nCrit;
    private double maxClCd;
    private String alpha;


    public Characteristics(String coordinatesStl, String fileNameId) {
        this.coordinatesStl = coordinatesStl;
        this.fileName = fileNameId;
        this.id = fileNameId.hashCode();
    }

    public Characteristics() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Characteristics that = (Characteristics) o;

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

    public String getCoordinatesStl() {
        return coordinatesStl;
    }

    public void setCoordinatesStl(String coordinatesStl) {
        this.coordinatesStl = coordinatesStl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getRenolgs() {
        return renolgs;
    }

    public void setRenolgs(double renolgs) {
        this.renolgs = renolgs;
    }

    public double getMaxClCd() {
        return maxClCd;
    }

    public void setMaxClCd(double maxClCd) {
        this.maxClCd = maxClCd;
    }

    public double getNCrit() {
        return nCrit;
    }

    public void setNCrit(double NCrit) {
        this.nCrit = NCrit;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }
}
