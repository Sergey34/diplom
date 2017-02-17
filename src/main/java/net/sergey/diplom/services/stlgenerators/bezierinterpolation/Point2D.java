package net.sergey.diplom.services.stlgenerators.bezierinterpolation;

/**
 * Created by seko0716 on 2/17/2017.
 */
public class Point2D {
    private double x, y;

    Point2D() {
        this.x = this.y = 0.0;
    }

    Point2D(double x, double y) {
        x = x;
        y = y;
    }

    void normalize() {
        double l = Math.sqrt(this.x * this.x + this.y * this.y);
        this.x /= l;
        this.y /= l;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Point2D subtraction(Point2D point2D) {
        return plus(-point2D.getX(), -point2D.getY());
    }

    public Point2D plus(Point2D point2D) {
        return plus(point2D.getX(), point2D.getY());
    }

    private Point2D plus(double x, double y) {
        double xTmp = this.x + x;
        double yTmp = this.y + y;
        return new Point2D(xTmp, yTmp);
    }


    public Point2D multiplication(double l1) {
        return null;
    }
}
