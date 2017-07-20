package net.sergey.diplom.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Document
public class Authorities implements GrantedAuthority {
    private String username;
    private String authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Authorities that = (Authorities) o;
        return username != null ? username.equals(that.username) : that.username == null;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
