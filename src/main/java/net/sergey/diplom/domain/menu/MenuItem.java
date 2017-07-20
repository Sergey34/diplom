package net.sergey.diplom.domain.menu;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "name")
@Document
public class MenuItem {
    private String name;
    private String url;
}
