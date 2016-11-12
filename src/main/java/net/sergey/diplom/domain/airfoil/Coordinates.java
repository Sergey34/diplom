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

        if (id != that.id) return false;
        return coordinatesJson != null ? coordinatesJson.equals(that.coordinatesJson) : that.coordinatesJson == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (coordinatesJson != null ? coordinatesJson.hashCode() : 0);
        return result;
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
}
