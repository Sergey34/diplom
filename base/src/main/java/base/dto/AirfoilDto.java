package base.dto;

import base.domain.airfoil.Prefix;
import lombok.Data;

import javax.persistence.*;

@Data
public class AirfoilDto {
    @Column(name = "name")
    private String name;
    @Id
    @Column(name = "shortName")
    private String shortName;
    @Column(name = "coord", columnDefinition = "Text")
    private String coordView;
    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "prefix")
    private Prefix prefix;
}
