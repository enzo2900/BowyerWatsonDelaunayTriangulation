package utility.graph;

import utility.MapPoint;

import java.util.ArrayList;
import java.util.Objects;

public class Vertex implements MapPoint {
    public double x,y;
    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public int hashCode() {
        int hash = Objects.hash(x,y);
        return hash;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Vertex v = new Vertex(x,y);
        return v;
    }

    @Override
    public String toString() {
        return "Vertex : (" + x + "," + y +")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vertex v) {
            return v.x == x && v.y == y;
        }
        return false;
    }
}
