package net.sergey.diplom.model;

import net.sergey.diplom.domain.airfoil.Airfoil;

public class AirfoilDTO {
    private String name;

    private String description;

    private String image;

    public AirfoilDTO(Airfoil airfoil) {
        this.name = airfoil.getName();
        this.description = airfoil.getDescription();
        this.image = airfoil.getImage();
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
}
