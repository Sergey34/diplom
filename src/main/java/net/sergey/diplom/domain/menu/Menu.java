package net.sergey.diplom.domain.menu;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "header")
@Document
public class Menu {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String header;
    private List<MenuItem> items;
}
