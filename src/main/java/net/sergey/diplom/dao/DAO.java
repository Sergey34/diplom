package net.sergey.diplom.dao;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.domain.SettingLoadJSON;

import java.util.List;

public interface DAO {

    void saveSettings(Filter filter);

    List<SettingLoadJSON> loadSetting(String SQL);

    List loadParsersImplementation(String s);
}