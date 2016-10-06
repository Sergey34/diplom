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
    private String userName;

    @Column(name = "atribJson")
    private String atribJson;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setAtribJson(String atribJson) {
        this.atribJson = atribJson;
    }

    @Override
    public String toString() {
        return "Filter{" +
                ", userName='" + userName + '\'' +
                ", atribJson='" + atribJson + '\'' +
                '}';
    }
}
