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
    @Column(name = "id")
    private int id;
    @Column(name = "header")
    private String header;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "menuHeader_menuItem",
            joinColumns = {@JoinColumn(name = "headerId")},
            inverseJoinColumns = {@JoinColumn(name = "ItemId")})
    private List<MenuItem> menuItems;

    public Menu(String header) {
        this.header = header;
        this.id = header.hashCode();
    }

}
