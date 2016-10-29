package net.sergey.diplom.model;

import java.util.List;

public class AjaxResponseBody {

    private String msg;

    private String code;

    private List<Object> result;

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }

    public AjaxResponseBody() {
    }

    public AjaxResponseBody(String msg, String code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
