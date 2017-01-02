package base.domain.menu;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu", nullable = false)
    private Menu menu;

    public MenuItem(String name, String urlCode) {
        this.name = name;
        this.urlCode = urlCode;
        this.id = name.hashCode();
    }
}
