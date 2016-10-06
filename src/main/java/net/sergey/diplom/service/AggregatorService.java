package net.sergey.diplom.service;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.model.Settings;

public interface AggregatorService {

    void saveSettings(Filter filter);

    Settings loadSetting(String SQL);

    String showData();
}
