package net.sergey.diplom.domain.airfoil;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Prefix {
    @Id
    private int id;
    private char prefix;

    public Prefix(char prefix) {
        this.prefix = prefix;
        this.id = String.valueOf(prefix).hashCode();
    }

    public Prefix() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public char getPrefix() {
        return prefix;
    }

    public void setPrefix(char prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prefix prefix = (Prefix) o;

        return id == prefix.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Prefix{" +
                "id=" + id +
                ", prefix=" + prefix +
                '}';
    }
}
