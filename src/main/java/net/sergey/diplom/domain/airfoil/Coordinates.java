package net.sergey.diplom.domain.airfoil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "coordinatesJson")
    private String coordinatesJson;
    @Column(name = "fileName")
    private String fileName;
    @Column(name = "renolgs")
    private String renolgs;
    @Column(name = "nCrit")
    private String nCrit;
    @Column(name = "maxClCd")
    private String maxClCd;

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

    public void setNCrit(String NCrit) {
        this.nCrit = NCrit;
    }

    public String getRenolgs() {
        return renolgs;
    }

    public void setRenolgs(String renolgs) {
        this.renolgs = renolgs;
    }

    public String getnCrit() {
        return nCrit;
    }

    public void setnCrit(String nCrit) {
        this.nCrit = nCrit;
    }

    public String getMaxClCd() {
        return maxClCd;
    }

    public void setMaxClCd(String maxClCd) {
        this.maxClCd = maxClCd;
    }
}
