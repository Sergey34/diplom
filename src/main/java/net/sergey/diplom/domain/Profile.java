package net.sergey.diplom.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "imageUrl")
    private String imagerUrl;
    @Column(name = "prefix")
    private char prefix;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "profile_links",
            joinColumns = {@JoinColumn(name = "profileId")},
            inverseJoinColumns = {@JoinColumn(name = "linkId")})
    private Set<Link> links;

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

    public String getImagerUrl() {
        return imagerUrl;
    }

    public void setImagerUrl(String imagerUrl) {
        this.imagerUrl = imagerUrl;
    }

    public char getPrefix() {
        return prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }
}
