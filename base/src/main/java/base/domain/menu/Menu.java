package base.domain.menu;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menuHeader")
@Data
@NoArgsConstructor
public class Menu {
    @Id
    @Column(name = "id_menu")
    private int id_menu;
    @Column(name = "header")
    private String header;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuItem> menuItems;

    public Menu(String header) {
        this.header = header;
        this.id_menu = header.hashCode();
    }

}
