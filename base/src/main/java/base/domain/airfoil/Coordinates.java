package base.domain.airfoil;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coordinates")
@Data
@NoArgsConstructor
public class Coordinates {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "coordinatesJson", columnDefinition = "Text")
    private String coordinatesJson;
    @Column(name = "fileName")
    private String fileName;
    @Column(name = "renolgs")
    private String renolgs;
    @Column(name = "nCrit")
    private String nCrit;
    @Column(name = "maxClCd")
    private String maxClCd;

    public Coordinates(String coordinatesJson, String fileNameId) {
        this.coordinatesJson = coordinatesJson;
        this.fileName = fileNameId;
        this.id = fileNameId.hashCode();
    }
}
