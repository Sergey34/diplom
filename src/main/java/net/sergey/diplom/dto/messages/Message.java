package net.sergey.diplom.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Message {
    public static int SC_CONFLICT = 409;
    public static int SC_FORBIDDEN = 403;
    public static int SC_OK = 200;
    public static int SC_NOT_ACCEPTABLE = 406;
    public static int SC_NOT_IMPLEMENTED = 501;

    private String message;
    private int code;

}
