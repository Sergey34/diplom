package net.sergey.diplom.services.parser;

import net.sergey.diplom.domain.menu.Menu;
import net.sergey.diplom.domain.menu.MenuItem;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ParserMenu {
    List<Menu> parse(Collection<MenuItem> menuItemsInDB) throws IOException;
}
