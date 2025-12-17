package utility;

import utility.graph.Edge;
import utility.graph.Vertex;

import java.util.HashMap;

public class TriangleGraph {
    public Edge i;
    public Edge j;
    public Edge k;

    public HashMap<Vertex,TriangleGraph> connectedToV1;
    public HashMap<Vertex,TriangleGraph> connectedToV2;
    public HashMap<Vertex,TriangleGraph> connectedToV3;

    public TriangleGraph() {
            connectedToV1 = new HashMap<>();
            connectedToV2 = new HashMap<>();
            connectedToV3 = new HashMap<>();
    }
    public TriangleGraph(Edge i, Edge j, Edge k) {
        this.i = i;
        this.j=  j;
        this.k = k;
    }

    public TriangleGraph(MapPoint v1, MapPoint v2, MapPoint v3) {

    }
}
