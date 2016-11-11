package net.sergey.diplom.model;

import net.sergey.diplom.domain.airfoil.Airfoil;
import net.sergey.diplom.domain.airfoil.Links;

import java.util.Set;

public class AirfoilLink extends AirfoilAbstract {
    private Set<Links> links;

    public AirfoilLink(Airfoil airfoil) {
        super(airfoil);
        this.links = airfoil.getLinks();
    }

    public Set<Links> getLinks() {
        return links;
    }

    public void setLinks(Set<Links> links) {
        this.links = links;
    }
}
