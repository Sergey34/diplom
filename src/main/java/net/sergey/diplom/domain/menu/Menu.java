package net.sergey.diplom.domain.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@Document
public class Menu {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String header;
    private List<MenuItem> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        return header.equals(menu.header);
    }

    @Override
    public int hashCode() {
        return header.hashCode();
    }
}
