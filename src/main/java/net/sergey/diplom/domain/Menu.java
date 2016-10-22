package net.sergey.diplom.domain;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "menuHeader")
public class Menu {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "header")
    private String header;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "menuHeader_menuItem",
            joinColumns = {@JoinColumn(name = "headerId")},
            inverseJoinColumns = {@JoinColumn(name = "ItemId")})
    private Set<MenuItem> menuItems;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public Set<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
