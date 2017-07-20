package net.sergey.diplom.domain.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document
public class MenuItem {
    private String name;
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MenuItem menuItem = (MenuItem) o;
        return name.equals(menuItem.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
