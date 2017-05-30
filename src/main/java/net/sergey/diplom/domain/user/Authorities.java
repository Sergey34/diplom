package net.sergey.diplom.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;


@Document
public class Authorities implements GrantedAuthority {
    @Id
    private String username;
    private String authority;

    public Authorities() {
    }

    public Authorities(String role, String username) {
        this.username = username;
        this.authority = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Authorities that = (Authorities) o;

        return username != null ? username.equals(that.username) : that.username == null;

    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Authorities{" +
                "username='" + username + '\'' +
                ", authority='" + authority + '\'' +
                '}';
    }
}
