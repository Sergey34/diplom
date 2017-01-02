package base.domain.airfoil;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "airfoil")
@NoArgsConstructor
public class Airfoil {
    @Column(name = "name")
    private String name;
    @Id
    @Column(name = "shortName")
    private String shortName;
    @Column(name = "coord", columnDefinition = "Text")
    private String coordView;
    @Column(name = "description", columnDefinition = "Text")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefix")
    private Prefix prefix;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "airfoil_coordinates", joinColumns = {@JoinColumn(name = "id_airfoil")},
            inverseJoinColumns = {@JoinColumn(name = "id_coordinates", unique = true)})
    private Set<Coordinates> coordinates;


    public Airfoil(String name, String description, Prefix prefix, String shotName) {
        this.name = name;
        this.shortName = shotName;
        this.description = description;
        this.prefix = prefix;
    }
}
