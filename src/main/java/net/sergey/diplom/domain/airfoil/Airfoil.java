package net.sergey.diplom.domain.airfoil;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "airfoil")
public class Airfoil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "airfoil_links", joinColumns = {@JoinColumn(name = "id_airfoil")},
            inverseJoinColumns = {@JoinColumn(name = "id_links")})
    private Set<Link> links;


    public Airfoil() {
        this.links = new HashSet<>();
    }

    public Airfoil(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.links = new HashSet<>();
    }

    public void addLink(Link link) {
        this.links.add(link);
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

    public void setDescription(String descriptions) {
        this.description = descriptions;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airfoil airfoil = (Airfoil) o;

        if (id != airfoil.id) return false;
        if (name != null ? !name.equals(airfoil.name) : airfoil.name != null) return false;
        if (description != null ? !description.equals(airfoil.description) : airfoil.description != null)
            return false;
        if (image != null ? !image.equals(airfoil.image) : airfoil.image != null) return false;
        return links != null ? links.equals(airfoil.links) : airfoil.links == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }
}
