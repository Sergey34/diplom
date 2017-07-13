package net.sergey.diplom.domain.airfoil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(collection = "characteristics")
public class Characteristics {
    @Id
    private Integer id;
    private String coordinatesStl;
    private String fileName;
    private double renolgs;
    private double nCrit;
    private double maxClCd;
    private String alpha;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Characteristics that = (Characteristics) o;
        return Objects.equals(id, that.id);
    }
}
