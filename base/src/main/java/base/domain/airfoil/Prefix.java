package base.domain.airfoil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "prefix")
public class Prefix {
    @Id
    @Column(name = "id_prefix")
    private int id;
    @Column(name = "prefix")
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
        return id == prefix1.id && prefix == prefix1.prefix;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) prefix;
        return result;
    }
}
