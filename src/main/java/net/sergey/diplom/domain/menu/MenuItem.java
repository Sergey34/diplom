package net.sergey.diplom.domain.menu;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class MenuItem {
    @Id
    private int id;
    private String name;
    private String urlCode;

    public MenuItem(String name, String urlCode) {
        this.name = name;
        this.urlCode = urlCode;
        this.id = name.hashCode();
    }

    public MenuItem() {
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
