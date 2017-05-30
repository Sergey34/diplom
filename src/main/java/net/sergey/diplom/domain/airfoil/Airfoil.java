package net.sergey.diplom.domain.airfoil;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "airfoils")
public class Airfoil {
    @Id
    private String shortName;
    private String name;
    private String coordView;
    private String description;
    private Prefix prefix;
    @DBRef
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

        return shortName != null ? shortName.equals(airfoil.shortName) : airfoil.shortName == null;

    }

    @Override
    public int hashCode() {
        return shortName != null ? shortName.hashCode() : 0;
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
