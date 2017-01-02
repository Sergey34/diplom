package base.domain.airfoil;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "prefix")
@Data
@NoArgsConstructor
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
}
