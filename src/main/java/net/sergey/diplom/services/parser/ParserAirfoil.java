package net.sergey.diplom.services.parser;

import java.util.concurrent.Callable;

public interface ParserAirfoil extends Callable<Void> {
    void setFinish();

    @Override
    Void call() throws Exception;

    void setPrefix(String prefix);
}
