package utility;


public class Segment<T extends MapPoint> {

    public  T start;

    public  T end;

    public Segment(T p, T q) {
        this.start = p;
        this.end = q;
    }

    public double getSlope() {
        if(start.getX() == end.getX()) return Double.POSITIVE_INFINITY; // segment vertical
        double longueurX = end.getX() - start.getX();
        double longueurY = end.getY() - start.getY();
        double YFor1X = longueurY / longueurX;
        return YFor1X;
    }

    public double getXatY(double ySweep) {
        if (start.getY() == end.getY()) return Math.min(start.getX(), end.getX()); // segment horizontal
        return start.getX() + (ySweep - start.getY()) * (end.getX() - start.getX()) / (end.getY() - start.getY());
    }

    public double getYatX(double x) {
        if(start.getX() == end.getX()) return Math.min(start.getY(),end.getY()); // segment vertical
        double longueurX = end.getX() - start.getX();
        double longueurY = end.getY() - start.getY();
        double YFor1X = longueurY / longueurX;
        double xValueInSegment = x - start.getX();
        return start.getY()+xValueInSegment * YFor1X;
        //return start.getY() + (x - start.getX()) * (end.getY() - start.getY()) / (end.getX() - start.getX());
    }

    public T upperEndPoint() {
        if(start.getY() > end.getY()) {
            return start;
        } else if (start.getY() == end.getY() && start.getY() < end.getX()) {
            return start;
        }else {
            return end;
        }
    }

    public T lowerEndPoint() {
        if(start.getY() > end.getY()) {
            return end;
        } else if (start.getY() == end.getY() && start.getX() < end.getX()) {
            return end;
        }else {
            return start;
        }
    }

    public boolean intersect(Segment<T> s2) {
        double x1 = start.getX(), y1 =start.getY();
        double x2 = end.getX(), y2 = end.getY();
        double x3 = s2.start.getX(), y3 = s2.start.getY();
        double x4 = s2.end.getX(), y4 = s2.end.getY();

        double denom = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);

        if (denom == 0) {
            // segments parallèles ou colinéaires → pas d'intersection unique
            return false;
        }

        double px = ((x1*y2 - y1*x2)*(x3-x4) - (x1-x2)*(x3*y4 - y3*x4)) / denom;
        double py = ((x1*y2 - y1*x2)*(y3-y4) - (y1-y2)*(x3*y4 - y3*x4)) / denom;

        // Vérifie si l'intersection est **dans les deux segments**
        if (px < Math.min(x1, x2) - 1e-9 || px > Math.max(x1, x2) + 1e-9) return false;
        if (px < Math.min(x3, x4) - 1e-9 || px > Math.max(x3, x4) + 1e-9) return false;
        if (py < Math.min(y1, y2) - 1e-9 || py > Math.max(y1, y2) + 1e-9) return false;
        if (py < Math.min(y3, y4) - 1e-9 || py > Math.max(y3, y4) + 1e-9) return false;

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Segment s) {
            return  s.start.equals(this.start) && s.end.equals(this.end);
        }
        return false;

    }
}
