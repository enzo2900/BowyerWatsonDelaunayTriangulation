package utility;

import java.util.ArrayList;

public  class Triangle {
    public Vertex i;
    public Vertex j;
    public Vertex k;

    public ArrayList<HalfEdge> edges ;

    public int lieOnEdge;

    public Triangle(Vertex i, Vertex j, Vertex k) {
        this.i = i;
        this.j = j;
        this.k = k;
        edges = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Triangle t ) {
            return (t.i.equals(i) || t.i.equals(j) || t.i.equals(k))
                    &&  (t.j.equals(j) || t.j.equals(i) || t.j.equals(k))
                    && (t.k.equals(i) ||t.k.equals(j) || t.k.equals(k));
        }
        return false;
    }

    public Triangle(Point p0, Point pFarLeft, Point pFarRight) {
        i = new Vertex(p0.x,p0.y);
        j = new Vertex(pFarLeft.x,pFarLeft.y);
        k = new Vertex(pFarRight.x,pFarRight.y);
        edges = new ArrayList<>();
    }

    public Point getMiddlePoint() {
        double x = (i.x +j.x + k.x)/3;
        double y = ((i.y + j.y + k.y) /3);
        return new Point(x,y);
    }

    public boolean isPInside(Point p) {
        Vertex v = new Vertex(p.x,p.y);
        double aireABC = aire(i, j, k);
        double aire1 = aire(v, i, j);
        double aire2 = aire(v, j,k);
        double aire3 = aire(v, k, i);

        return Math.abs(aireABC - (aire1 + aire2 + aire3)) < 1e-9;
    }

    static double aire(Vertex A, Vertex B, Vertex C) {
        return Math.abs(
                (A.x * (B.y - C.y) +
                        B.x * (C.y - A.y) +
                        C.x * (A.y - B.y)) / 2.0
        );
    }

    public int isPinCircle() {
        return lieOnEdge;
    }
}
