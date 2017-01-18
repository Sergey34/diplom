package net.sergey.diplom.dto.messages;

public class Message {
    public static int SC_CONFLICT = 409;
    public static int SC_FORBIDDEN = 403;
    public static int SC_OK = 200;
    public static int SC_NOT_ACCEPTABLE = 406;
    public static int SC_NOT_IMPLEMENTED = 501;

    private String message;
    private int code;

    public Message(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public Message() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
