package net.sergey.diplom.service;

import net.sergey.diplom.domain.Menu;
import net.sergey.diplom.domain.User;

import java.io.IOException;
import java.util.List;

public interface Service {

    List<Menu> getMenu() throws IOException;

    Object getAirfoilsByLiteral(String literal) throws IOException;

    List<User> getUser(String alex);
}
