package net.sergey.diplom.dto.messages;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageError extends Message {
    private StackTraceElement[] stackTrace;

    public MessageError(String s, int scNotImplemented, StackTraceElement[] stackTrace) {
        super(s, scNotImplemented);
        this.stackTrace = stackTrace;
    }
}
