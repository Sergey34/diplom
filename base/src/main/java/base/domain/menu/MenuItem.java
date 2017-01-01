package base.domain.menu;


import base.domain.airfoil.Prefix;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "menuItem")
@Data
@NoArgsConstructor
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

    public static MenuItem createMenuItemByNewPrefix(Prefix prefix) {
        return new MenuItem(String.valueOf(prefix.getPrefix()), String.valueOf(prefix.getPrefix()));
    }
}
