package net.sergey.diplom.services.stlgenerators.bezierinterpolation;

import java.util.List;

public class BezierInterpolation /*implements Interpolation*/ {
    private static final double EPSILON = 1.0e-5;
    public static final double RESOLUTION = 32;

    boolean calculateSpline(List<Point2D> values, List<Segment> bezier) {
        int n = values.size() - 1;

        if (n < 2)
            return false;

        //bezier.resize(n);//??

        Point2D tgL = new Point2D();
        Point2D tgR = new Point2D();
        Point2D cur;
        Point2D next = values.get(1).subtraction(values.get(0));
        next.normalize();

        double l1, l2, tmp, x;

        --n;

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
                    x = (values.get(i + 1).getY() - tgR.getY() / tgR.getX() * values.get(i + 1).getX() - values.get(i).getY() + tgL.getY() / tgL.getX() * values.get(i).getX()) / tmp;
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
        }

        l1 = Math.abs(tgL.getX()) > EPSILON ? (values.get(n + 1).getX() - values.get(n).getX()) / (2.0 * tgL.getX()) : 1.0;

        bezier.get(n).setPoint(0, values.get(n));
        bezier.get(n).setPoint(1, values.get(n));
        bezier.get(n).setPoint(2, values.get(n + 1));
        bezier.get(n).setPoint(3, values.get(n + 1));
        bezier.get(n).setPoint(1, bezier.get(n).getPoint(1).plus(tgR.multiplication(l1)));

        return true;
    }
}


