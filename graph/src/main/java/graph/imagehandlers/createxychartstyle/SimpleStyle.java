package graph.imagehandlers.createxychartstyle;


import graph.imagehandlers.StyleXYChart;
import org.knowm.xchart.XYChart;

public class SimpleStyle implements StyleXYChart {
    @Override
    public XYChart getXYChart() {
        return new XYChart(700, 400);
    }
}
