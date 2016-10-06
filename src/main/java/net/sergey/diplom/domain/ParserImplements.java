package net.sergey.diplom.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by sergey on 22.02.16.
 */
@Entity
@Table(name = "parsers")
public final class ParserImplements {
    public String getParserImplements() {
        return parserImplements;
    }

    public void setParserImplements(String parserImplements) {
        this.parserImplements = parserImplements;
    }

    @Id
    @Column(name = "Implementation")
    private String parserImplements;
}
