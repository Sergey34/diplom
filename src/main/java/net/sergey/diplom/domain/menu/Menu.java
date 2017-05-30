package net.sergey.diplom.domain.menu;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Menu {
    @Id
    private int id;
    private String header;
    private List<MenuItem> menuItems;

    public Menu() {
    }

    public Menu(String header) {
        this.header = header;
        this.id = header.hashCode();
    }

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


    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Menu menu = (Menu) o;

        return id == menu.id;

    }

    @Override
    public int hashCode() {
        return id;
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
