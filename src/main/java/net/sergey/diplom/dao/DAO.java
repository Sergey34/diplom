package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.ParserImplements;

import java.util.List;

public interface DAO {

    void saveSettings(Filter filter);

    List<Filter> loadSetting(String SQL);

    List<ParserImplements> loadParsersImplementation();
}