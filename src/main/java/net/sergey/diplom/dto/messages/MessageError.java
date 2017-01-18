package net.sergey.diplom.dto.messages;

public class MessageError extends Message {
    private StackTraceElement[] stackTrace;

    public MessageError(String message, int code, StackTraceElement[] stackTrace) {
        super(message, code);
        this.stackTrace = stackTrace;
    }

    public MessageError() {
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }
}
