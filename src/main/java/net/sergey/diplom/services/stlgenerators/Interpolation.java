package net.sergey.diplom.services.stlgenerators;

import net.sergey.diplom.services.stlgenerators.cubespline.CubeSpline;

import java.util.List;

public interface Interpolation {
    CubeSpline BuildSpline(List<Double> t, List<Double> coord);

    List<Double> applySpline();
}
