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
public final class SettingLoadJSON {
    @Id
    @Column(name = "atribJson")
    private String filterJson;

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }
}
