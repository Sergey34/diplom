package net.sergey.diplom.services.stlgenerators.cubespline;

import net.sergey.diplom.services.stlgenerators.Interpolation;

import java.util.ArrayList;
import java.util.List;

public class CubeSpline implements Interpolation {

    private List<SplineTuple> splines;
    private List<Double> t;


    @Override
    public CubeSpline BuildSpline(List<Double> t, List<Double> coord) {
        this.t = t;
        splines = new ArrayList<>(coord.size());

        for (int i = 0; i < coord.size(); ++i) {
            splines.add(new SplineTuple(coord.get(i), t.get(i)));
        }
        splines.get(0).c = splines.get(coord.size() - 1).c = 0.0;

        // Решение СЛАУ относительно коэффициентов сплайнов c[i] методом прогонки для трехдиагональных матриц
        // Вычисление прогоночных коэффициентов - прямой ход метода прогонки
        List<Double> alpha = new ArrayList<>(coord.size() - 1);
        List<Double> beta = new ArrayList<>(coord.size() - 1);
        alpha.add(0, 0.0);
        beta.add(0, 0.0);
        for (int i = 1; i < coord.size() - 1; ++i) {
            double h_i = t.get(i) - t.get(i - 1), h_i1 = t.get(i + 1) - t.get(i);
            double C = 2.0 * (h_i + h_i1);
            double F = 6.0 * ((coord.get(i + 1) - coord.get(i)) / h_i1 - (coord.get(i) - coord.get(i - 1)) / h_i);
            double z = (h_i * alpha.get(i - 1) + C);
            alpha.add(i, -h_i1 / z);
            beta.add(i, (F - h_i * beta.get(i - 1)) / z);
        }

        // Нахождение решения - обратный ход метода прогонки
        for (int i = coord.size() - 2; i > 0; --i)
            splines.get(i).c = alpha.get(i) * splines.get(i + 1).c + beta.get(i);

        // По известным коэффициентам c[i] находим значения b[i] и d[i]
        for (int i = coord.size() - 1; i > 0; --i) {
            double h_i = t.get(i) - t.get(i - 1);
            splines.get(i).d = (splines.get(i).c - splines.get(i - 1).c) / h_i;
            splines.get(i).b =
                    h_i * (2.0 * splines.get(i).c + splines.get(i - 1).c) / 6.0 + (coord.get(i) - coord.get(i - 1)) / h_i;
        }
        return this;
    }

    private double calculateValue(double x) {
        SplineTuple s;
        int n = splines.size();
        if (x <= splines.get(0).x) { // Если t меньше точки сетки t[0] - пользуемся первым эл-тов массива
            s = splines.get(1);
        } else if (x >= splines.get(n - 1).x) { // Если t больше точки сетки t[n - 1] - пользуемся последним эл-том массива
            s = splines.get(n - 1);
        } else { // t лежит между граничными точками сетки - производим бинарный поиск нужного эл-та массива
            int i = 0, j = n - 1;
            while (i + 1 < j) {
                int k = i + (j - i) / 2;
                if (x <= splines.get(k).x) {
                    j = k;
                } else {
                    i = k;
                }
            }
            s = splines.get(j);
        }

        double dx = (x - s.x);
        // Вычисляем значение сплайна в заданной точке по схеме Горнера
        return s.a + (s.b + (s.c / 2.0 + s.d * dx / 6.0) * dx) * dx;
    }

    @Override
    public List<Double> applySpline() {
        List<Double> listTmp = new ArrayList<>();
        for (int i = 0; i < t.size() - 1; i++) {
            Double start = t.get(i);
            double v = (t.get(i + 1) - start) / 4;
            listTmp.add(this.calculateValue(start));
            for (int j = 1; j < 4; j++) {
                listTmp.add(this.calculateValue(start + j * v));
            }
        }
        listTmp.add(this.calculateValue(t.get(t.size() - 1)));
        return listTmp;
    }

    private class SplineTuple {
        final double a;
        final double x;
        double b;
        double c;
        double d;

        SplineTuple(double a, double x) {
            this.a = a;
            this.x = x;
        }
    }
}
