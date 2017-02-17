package net.sergey.diplom.services.stlgenerators;

import java.util.List;

public interface Interpolation {
    void BuildSpline(List<Double> t, List<Double> coord, int n);
    double calculateValue(double x);
}
