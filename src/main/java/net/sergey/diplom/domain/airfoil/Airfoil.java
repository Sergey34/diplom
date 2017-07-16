package net.sergey.diplom.domain.airfoil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import java.util.Set;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document(collection = "airfoils")
public class Airfoil {
    @Id
    @Column(nullable = false, unique = true)
    private String shortName;
    private String name;
    private String coordView;
    private String description;
    private char prefix;
    @DBRef
    private Set<Characteristics> characteristics;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Airfoil airfoil = (Airfoil) o;
        return shortName != null ? shortName.equals(airfoil.shortName) : airfoil.shortName == null;
    }

    @Override
    public int hashCode() {
        return shortName != null ? shortName.hashCode() : 0;
    }
}
