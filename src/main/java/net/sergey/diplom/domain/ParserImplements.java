package net.sergey.diplom.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "parsers")
public final class ParserImplements {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "Implementation")
    private String parserImplements;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParserImplements() {
        return parserImplements;
    }

    public void setParserImplements(String parserImplements) {
        this.parserImplements = parserImplements;
    }
}
