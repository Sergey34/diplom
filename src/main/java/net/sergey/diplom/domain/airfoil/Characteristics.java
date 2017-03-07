package net.sergey.diplom.domain.airfoil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "characteristics")
public class Characteristics {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "coordinatesStl", columnDefinition = "Text")
    private String coordinatesStl;
    @Column(name = "fileName")
    private String fileName;
    @Column(name = "renolgs")
    private String renolgs;
    @Column(name = "nCrit")
    private String nCrit;
    @Column(name = "maxClCd")
    private String maxClCd;
    @Column(name = "alpha")
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

    public String getcoordinatesStl() {
        return coordinatesStl;
    }

    public void setcoordinatesStl(String coordinatesStl) {
        this.coordinatesStl = coordinatesStl;
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
