package net.sergey.diplom.services.utils.imagehandlers;

import java.util.List;


public class Xy {
    private final String legend;
    private List<Double> x;
    private List<Double> y;

    public Xy(List<Double> x, List<Double> y, String legend) {
        this.legend = legend;
        this.x = x;
        this.y = y;
    }

    public List<Double> getX() {
        return x;
    }

    public void setX(List<Double> x) {
        this.x = x;
    }

    public List<Double> getY() {
        return y;
    }

    public void setY(List<Double> y) {
        this.y = y;
    }

    public String getLegend() {
        return legend;
    }
}