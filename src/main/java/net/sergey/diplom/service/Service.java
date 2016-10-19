package net.sergey.diplom.service;

import net.sergey.diplom.domain.Filter;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;

import java.io.IOException;
import java.util.Map;

public interface Service {

    void saveSettings(Filter filter);

    Settings loadSetting(String SQL);

    String showData(String name);

    Parser getBeanParserByName(String parserImplementation);

    Map<String, String> getMenu() throws IOException;

    Object getAirfoilsByLiteral(String literal) throws IOException;
}
