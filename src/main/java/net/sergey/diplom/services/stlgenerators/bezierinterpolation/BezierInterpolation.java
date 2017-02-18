package net.sergey.diplom.services.stlgenerators.bezierinterpolation;

import net.sergey.diplom.services.stlgenerators.Interpolation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component(value = "bezier")
public class BezierInterpolation implements Interpolation {
    public static final double RESOLUTION = 32;
    private static final double EPSILON = 1.0e-5;
    private List<Segment> spline;

    private BezierInterpolation calculateSpline(List<Point2D> values) {
        int n = values.size() - 1;
        if (n < 2) {
            return null;
        }
        List<Segment> bezier = resize(n);

        Point2D tgL = new Point2D();
        Point2D tgR = new Point2D();
        Point2D cur;
        Point2D next = values.get(1).subtraction(values.get(0));
        next.normalize();
        double l1, l2, tmp, x;
        n--;
        for (int i = 0; i < n; ++i) {
            bezier.get(i).setPoint(0, values.get(i));
            bezier.get(i).setPoint(1, values.get(i));
            bezier.get(i).setPoint(2, values.get(i + 1));
            bezier.get(i).setPoint(3, values.get(i + 1));

            cur = next;
            next = values.get(i + 2).subtraction(values.get(i + 1));
            next.normalize();

            tgL = tgR;

            tgR = cur.plus(next);
            tgR.normalize();

            if (Math.abs(values.get(i + 1).getY() - values.get(i).getY()) < EPSILON) {
                l1 = l2 = 0.0;
            } else {
                tmp = values.get(i + 1).getX() - values.get(i).getX();
                l1 = Math.abs(tgL.getX()) > EPSILON ? tmp / (2.0 * tgL.getX()) : 1.0;
                l2 = Math.abs(tgR.getX()) > EPSILON ? tmp / (2.0 * tgR.getX()) : 1.0;
            }

            if (Math.abs(tgL.getX()) > EPSILON && Math.abs(tgR.getX()) > EPSILON) {
                tmp = tgL.getY() / tgL.getX() - tgR.getY() / tgR.getX();
                if (Math.abs(tmp) > EPSILON) {
                    x = (values.get(i + 1).getY() - tgR.getY() / tgR.getX() * values.get(i + 1).getX()
                            - values.get(i).getY() + tgL.getY() / tgL.getX() * values.get(i).getX()) / tmp;
                    if (x > values.get(i).getX() && x < values.get(i + 1).getX()) {
                        if (tgL.getY() > 0.0) {
                            if (l1 > l2)
                                l1 = 0.0;
                            else
                                l2 = 0.0;
                        } else {
                            if (l1 < l2)
                                l1 = 0.0;
                            else
                                l2 = 0.0;
                        }
                    }
                }
            }

            bezier.get(i).setPoint(1, bezier.get(i).getPoint(1).plus(tgL.multiplication(l1)));
            bezier.get(i).setPoint(2, bezier.get(i).getPoint(2).subtraction(tgR.multiplication(l2)));
            System.out.println(bezier);
        }

        l1 = Math.abs(tgL.getX()) > EPSILON ? (values.get(n + 1).getX() - values.get(n).getX()) / (2.0 * tgL.getX()) : 1.0;

        bezier.get(n).setPoint(0, values.get(n));
        bezier.get(n).setPoint(1, values.get(n));
        bezier.get(n).setPoint(2, values.get(n + 1));
        bezier.get(n).setPoint(3, values.get(n + 1));
        bezier.get(n).setPoint(1, bezier.get(n).getPoint(1).plus(tgR.multiplication(l1)));
        this.spline = bezier;
        return this;
    }

    private List<Segment> resize(int n) {
        List<Segment> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(Segment.getDefaultSegment());
        }
        return list;

    }

    @Override
    public Interpolation BuildSplineForLists(List<Double> x, List<Double> y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException("списки координат долны быть равной длины");
        }

        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            points.add(new Point2D(x.get(i), y.get(i)));
        }

        calculateSpline(points);
        return this;
    }

    @Override
    public List<Point2D> applySpline() {
        Point2D p = new Point2D();
        List<Point2D> points = new ArrayList<>();
        for (Segment s : spline) {
            for (int i = 0; i < RESOLUTION; ++i) {
                s.calc((double) i / RESOLUTION, p);
                System.out.println(p.getX() + " " + p.getY());
                points.add(new Point2D(p));
            }
        }
        return points;
    }

    @Override
    public String getName() {
        return "bezier";
    }
}


