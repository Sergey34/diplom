package net.sergey.diplom.domain.airfoil;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prefix")
public class Prefix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "prefix", unique = true)
    private char prefix;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "prefix_airfoil", joinColumns = {@JoinColumn(name = "id_prefix")},
            inverseJoinColumns = {@JoinColumn(name = "id_airfoil")})
    private Set<Airfoil> airfoils;


    public Prefix(char prefix) {
        this.prefix = prefix;
        this.airfoils = new HashSet<>();
    }

    public Prefix() {
        this.airfoils = new HashSet<>();
    }

    public void addAirfoils(Airfoil airfoil) {
        this.airfoils.add(airfoil);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getPrefix() {
        return prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public Set<Airfoil> getAirfoils() {
        return airfoils;
    }

    public void setAirfoils(Set<Airfoil> airfoils) {
        this.airfoils = airfoils;
    }
}
