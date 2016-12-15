package net.sergey.diplom.domain.model;

import net.sergey.diplom.domain.airfoil.Airfoil;

public class AirfoilDTO {
    private String name;
    private String description;
    private String image;
    private String shortName;

    public AirfoilDTO(Airfoil airfoil) {
        this.name = airfoil.getName();
        this.description = airfoil.getDescription();
        this.image = airfoil.getImage();
        this.shortName = airfoil.getShortName();
    }

    public AirfoilDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
