package net.sergey.diplom.dto;

public class Condition {
    private String action;
    private String attrName;
    private double value;

    public Condition(String action, String attrName, double value) {
        this.action = action;
        this.attrName = attrName;
        this.value = value;
    }

    public Condition() {
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
