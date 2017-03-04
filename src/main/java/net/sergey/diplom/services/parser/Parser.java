package net.sergey.diplom.services.parser;

import net.sergey.diplom.dto.messages.Message;

import java.util.concurrent.Future;

public interface Parser {

    Future<Message> startParsing();

    Message stopParsing();

    boolean parsingIsStarting();
}
