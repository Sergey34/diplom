package net.sergey.diplom.domain.menu;

import net.sergey.diplom.domain.airfoil.Prefix;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "menuItem")
public class MenuItem {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "url")
    private String urlCode;

    public MenuItem(String name, String urlCode) {
        this.name = name;
        this.urlCode = urlCode;
        this.id = name.hashCode();
    }

    public MenuItem() {
    }

    public static MenuItem createMenuItemByNewPrefix(Prefix prefix) {
        return new MenuItem(String.valueOf(prefix.getPrefix()), String.valueOf(prefix.getPrefix()));
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

    public String getUrlCode() {
        return urlCode;
    }

    public void setUrlCode(String url) {
        this.urlCode = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItem menuItem = (MenuItem) o;

        if (id != menuItem.id) return false;
        if (name != null ? !name.equals(menuItem.name) : menuItem.name != null) return false;
        return urlCode != null ? urlCode.equals(menuItem.urlCode) : menuItem.urlCode == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (urlCode != null ? urlCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + urlCode + '\'' +
                '}';
    }
}
