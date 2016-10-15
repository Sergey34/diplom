package net.sergey.diplom.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by sergey on 20.02.16.
 */
@Entity
@Table(name = "filter")
public final class Filter {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "atribJson")
    private String atribJson;

    public String getAtribJson() {
        return atribJson;
    }

    public void setAtribJson(String atribJson) {
        this.atribJson = atribJson;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    @Override
    public String toString() {
        return "Filter{" +
                ", userName='" + username + '\'' +
                ", atribJson='" + atribJson + '\'' +
                '}';
    }
}
