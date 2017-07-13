package net.sergey.diplom.services.buildergraphs.imagehandlers;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@lombok.Data
@AllArgsConstructor
@Builder
public class Xy {
    private final String legend;
    private List<Double> x;
    private List<Double> y;

    public Xy(List<Double> x, List<Double> y, String legend) {
        this.legend = legend;
        this.x = x;
        this.y = y;
    }
}