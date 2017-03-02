package net.sergey.diplom.dto;

public class Condition {
    private String action;
    private String attrName;
    private String value;

    public Condition(String action, String attrName, String value) {
        this.action = action;
        this.attrName = attrName;
        this.value = value;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
