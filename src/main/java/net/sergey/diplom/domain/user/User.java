package net.sergey.diplom.domain.user;

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
@EqualsAndHashCode(of = "userName")
@Document
public class User {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String userName;
    private String password;
    private boolean enabled;
    private List<Authorities> authorities;
}
