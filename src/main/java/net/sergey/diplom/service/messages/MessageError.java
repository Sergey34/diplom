package net.sergey.diplom.service.messages;

public class MessageError extends Message {
    private String errorInfo;

    public MessageError() {
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
