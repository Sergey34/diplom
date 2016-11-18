package net.sergey.diplom.service.utils.imagehandlers.createxychartstyle;

import net.sergey.diplom.service.utils.imagehandlers.StyleXYChart;
import org.knowm.xchart.XYChart;

public class SimpleStyle implements StyleXYChart {
    @Override
    public XYChart getXYChart() {
        return new XYChart(700, 400);
    }
}
