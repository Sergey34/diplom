package net.sergey.diplom.services.buildergraphs.imagehandlers.createxychartstyle;

import net.sergey.diplom.services.buildergraphs.imagehandlers.StyleXYChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

public class MinimalStyle implements StyleXYChart {
    @Override
    public XYChart getXYChart() {
        XYChart chart = new XYChart(900, 144, XYStyler.ChartTheme.Matlab);
        chart.getStyler().setAxisTicksVisible(false).setYAxisTicksVisible(false).setLegendVisible(false);
        return chart;
    }
}
