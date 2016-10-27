package net.sergey.diplom.service;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.Profile;
import net.sergey.diplom.domain.User;
import net.sergey.diplom.model.Settings;
import net.sergey.diplom.service.Parsers.Parser;

import java.io.IOException;
import java.util.List;

public interface Service {

    void saveSettings(Profile filter);

    Settings loadSetting(String SQL);

    String showData(String name);

    Parser getBeanParserByName(String parserImplementation);

    List<Menu> getMenu() throws IOException;

    Object getAirfoilsByLiteral(String literal) throws IOException;

    List<User> getUser(String alex);
}
