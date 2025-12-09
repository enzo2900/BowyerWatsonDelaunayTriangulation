package utility;

public class Vertex implements MapPoint {

    public double x,y;

    public HalfEdge incidentEdge;

    public int tag;

    public int i;

    private static int counter = 0;

    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
        counter ++;
        i = counter;
    }

    public Vertex copy() {
        return new Vertex(x,y);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vertex v) {
            return v.x == x && v.y == y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Vertex ("+ i + ") "+
                 + x + ","+
                + y +
                ", next= " + incidentEdge.next.v.i +
                ", prev= " + incidentEdge.prev.v.i+
                ", tag=" + tag
                ;
    }

    public static void resetCounter() {
        counter = 0;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
