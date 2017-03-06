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

        Prefix prefix1 = (Prefix) o;

        if (id != prefix1.id) return false;
        return prefix == prefix1.prefix;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) prefix;
        return result;
    }

    @Override
    public String toString() {
        return "Prefix{" +
                "id=" + id +
                ", prefix=" + prefix +
                '}';
    }
}
