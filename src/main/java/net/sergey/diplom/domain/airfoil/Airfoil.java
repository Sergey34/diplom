package net.sergey.diplom.domain.airfoil;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "shortName")
@Document(collection = "airfoils")
public class Airfoil {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String shortName;
    private String name;
    private String coordView;
    private String description;
    private char prefix;
    @DBRef(lazy = true)
    private Set<Characteristics> characteristics;
}
