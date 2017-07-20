package net.sergey.diplom.domain.airfoil;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "fileName")
@Document(collection = "characteristics")
public class Characteristics {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String fileName;
    private String coordinatesStl;
    private double renolds;
    private double nCrit;
    private double maxClCd;
    private String alpha;
}
