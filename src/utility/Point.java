package utility;


public class Point implements MapPoint {

    public double x;
    public double y ;


    public Point(double x,double y) {
        this.x = x;
        this.y = y;
    }

    public Point betweenPoint(Point p) {
        return new Point((x+p.x) /2,(y+p.y)/2);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Point p ) {
            return p.x==x && p.y == y;
        }
        return false;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
