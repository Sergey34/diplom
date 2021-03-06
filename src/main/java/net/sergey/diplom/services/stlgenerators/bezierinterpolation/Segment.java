package net.sergey.diplom.services.stlgenerators.bezierinterpolation;

class Segment {

    private Point2D[] points = new Point2D[4];

    static Segment getDefaultSegment() {
        Segment segment = new Segment();
        for (int i = 0; i < 4; i++) {
            segment.setPoint(i, new Point2D());
        }
        return segment;
    }

    void calc(double t, Point2D p) {
        double t2 = t * t;
        double t3 = t2 * t;
        double nt = 1.0 - t;
        double nt2 = nt * nt;
        double nt3 = nt2 * nt;
        p.setX(nt3 * points[0].getX() + 3.0 * t * nt2 * points[1].getX() + 3.0 * t2 * nt * points[2].getX() + t3 * points[3].getX());
        p.setY(nt3 * points[0].getY() + 3.0 * t * nt2 * points[1].getY() + 3.0 * t2 * nt * points[2].getY() + t3 * points[3].getY());
    }

    Point2D getPoint(int i) {
        return points[i];
    }

    void setPoint(int i, Point2D point2D) {
        this.points[i] = point2D;
    }
}