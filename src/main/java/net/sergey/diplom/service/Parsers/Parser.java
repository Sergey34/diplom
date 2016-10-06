package net.sergey.diplom.service.Parsers;

import java.io.IOException;

/**
 * Created by sergey on 16.02.16.
 */
public interface Parser {
    String getData(String jsonText) throws IOException;

    String update(String jsonText) throws IOException;
}
