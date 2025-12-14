package utility;

import utility.graph.Edge;
import utility.graph.Vertex;

public class TriangleGraph {
    public Edge i;
    public Edge j;
    public Edge k;

    public TriangleGraph() {

    }
    public TriangleGraph(Edge i, Edge j, Edge k) {
        this.i = i;
        this.j=  j;
        this.k = k;
    }

    public TriangleGraph(MapPoint v1, MapPoint v2, MapPoint v3) {

    }
}
