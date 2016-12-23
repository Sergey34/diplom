package net.sergey.diplom.domain.airfoil;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "airfoil")
public class Airfoil {
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "name")
    private String name;
    @Column(name = "shortName")
    private String shortName;
    @Column(name = "coord")
    private String coordView;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "image")
    private String image;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefix")
    private Prefix prefix;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "airfoil_coordinates", joinColumns = {@JoinColumn(name = "id_airfoil")},
            inverseJoinColumns = {@JoinColumn(name = "id_coordinates", unique = true)})
    private Set<Coordinates> coordinates;

    public Airfoil(String name, String description, Prefix prefix, String shortName) {
        this.name = name;
        this.description = description;
        this.image = "/resources/airfoil_img/" + shortName + ".png";//// TODO: 17.11.16 убрать в TDO
        this.prefix = prefix;
        this.id = shortName.hashCode();
        this.shortName = shortName;
    }

    public Airfoil() {
    }

    public Airfoil(String name, String description, String image, int id, Prefix prefix) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.id = id;
        this.prefix = prefix;
    }

    public Airfoil(String name, String details, String shortName) {
        this.name = name;
        this.shortName = shortName;
        this.description = details;
        this.image = "/resources/airfoil_img/" + shortName + ".png";//// TODO: 17.11.16 убрать в TDO
        this.id = shortName.hashCode();
    }

    public Set<Coordinates> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Set<Coordinates> coordinates) {
        this.coordinates = coordinates;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefix prefix) {
        this.prefix = prefix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airfoil airfoil = (Airfoil) o;

        if (id != airfoil.id) return false;
        if (name != null ? !name.equals(airfoil.name) : airfoil.name != null) return false;
        if (description != null ? !description.equals(airfoil.description) : airfoil.description != null) return false;
        if (image != null ? !image.equals(airfoil.image) : airfoil.image != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
        this.image = "/resources/airfoil_img/" + shortName + ".png";//// TODO: 17.11.16 убрать в TDO
    }

    public String getCoordView() {
        return coordView;
    }

    public void setCoordView(String coordView) {
        this.coordView = coordView;
    }
}
