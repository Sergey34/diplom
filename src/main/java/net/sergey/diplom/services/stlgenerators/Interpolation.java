package net.sergey.diplom.services.stlgenerators;

import net.sergey.diplom.services.stlgenerators.bezierinterpolation.Point2D;

import java.util.List;

public interface Interpolation {

    Interpolation buildSplineForLists(List<Double> x, List<Double> y);

    List<Point2D> applySpline();

    String getName();
}
