package graph.service.createxychartstyle;


import graph.service.StyleXYChart;
import org.knowm.xchart.XYChart;

public class SimpleStyle implements StyleXYChart {
    @Override
    public XYChart getXYChart() {
        return new XYChart(700, 400);
    }
}
