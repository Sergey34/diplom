package net.sergey.diplom.domain;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "menuHeader")
public class Menu {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "header")
    private String header;


    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu menu = (Menu) o;

        if (id != menu.id) return false;
        if (header != null ? !header.equals(menu.header) : menu.header != null) return false;
        return menuItems != null ? menuItems.equals(menu.menuItems) : menu.menuItems == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + (menuItems != null ? menuItems.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", menuItems=" + menuItems +
                '}';
    }
}
