package net.sergey.diplom.services.stlgenerators;

import net.sergey.diplom.services.stlgenerators.cubespline.Spline;

import java.util.List;

public interface Interpolation {
    void BuildSpline(List<Double> t, List<Double> coord, int n);
    List<Double> applySpline(List<Double> t, Spline spline);
}
