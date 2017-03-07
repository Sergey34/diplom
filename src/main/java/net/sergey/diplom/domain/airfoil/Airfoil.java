package net.sergey.diplom.domain.airfoil;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "airfoil", indexes = {
        @Index(columnList = "shortName", name = "shortName", unique = true),
        @Index(columnList = "prefix", name = "prefix")
})
public class Airfoil {
    @Column(name = "name")
    private String name;
    @Id
    @Column(name = "shortName", length = 128)
    private String shortName;
    @Column(name = "coord", columnDefinition = "Text")
    private String coordView;
    @Column(name = "description", columnDefinition = "Text")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefix")
    private Prefix prefix;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "airfoil_coordinates",
            joinColumns = {@JoinColumn(name = "id_airfoil")},
            inverseJoinColumns = {@JoinColumn(name = "id_coordinates")})
    private Set<Characteristics> characteristics;


    public Airfoil(String name, String description, Prefix prefix, String shortName) {
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.shortName = shortName;
    }

    public Airfoil() {
    }

    public Airfoil(String name, String details, String shortName) {
        this.name = name;
        this.shortName = shortName;
        this.prefix = new Prefix(shortName.toUpperCase().charAt(0));
        this.description = details;
    }

    public Set<Characteristics> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Set<Characteristics> characteristics) {
        this.characteristics = characteristics;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    public void setPrefix(Prefix prefix) {
        this.prefix = prefix;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airfoil airfoil = (Airfoil) o;

        if (name != null ? !name.equals(airfoil.name) : airfoil.name != null) return false;
        if (shortName != null ? !shortName.equals(airfoil.shortName) : airfoil.shortName != null) return false;
        if (coordView != null ? !coordView.equals(airfoil.coordView) : airfoil.coordView != null) return false;
        if (description != null ? !description.equals(airfoil.description) : airfoil.description != null) return false;
        if (prefix != null ? !prefix.equals(airfoil.prefix) : airfoil.prefix != null) return false;
        return characteristics != null ? characteristics.equals(airfoil.characteristics) : airfoil.characteristics == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (coordView != null ? coordView.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (characteristics != null ? characteristics.hashCode() : 0);
        return result;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCoordView() {
        return coordView;
    }

    public void setCoordView(String coordView) {
        this.coordView = coordView;
    }

    @Override
    public String toString() {
        return "Airfoil{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", prefix=" + prefix +
                '}';
    }
}
