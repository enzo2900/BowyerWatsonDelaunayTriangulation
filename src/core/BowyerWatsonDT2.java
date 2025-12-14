package core;


import utility.GeometryUtility;
import utility.Point;
import utility.TriangleGraph;
import utility.graph.Edge;
import utility.graph.Graph2DTopologyBuilder;
import utility.graph.Vertex;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Une implémentation plus simpliste que l'approche incrémentale cité dans le livre Computational Géometry.
 * Utiliser l'article pour l'implémenter, une recherche plus approfondie peut être nécessaire.
 * <a href="https://en.wikipedia.org/wiki/Bowyer%E2%80%93Watson_algorithm">Watson</a>
 */
public class BowyerWatsonDT2 {

    public static ArrayList<TriangleGraph> compute(ArrayList<Point> points) {
        var builder = Graph2DTopologyBuilder.builder();
        builder.addEdge(new Vertex(0,100),new Vertex(-100,-100))
                .addEdge(new Vertex(-100,-100),new Vertex(100,-100))
                .addEdge(new Vertex(100,-100),new Vertex(0,100));
        var triangleGraphs = getTriangles(builder);

        for(Point point : points) {
            ArrayList<TriangleGraph> badTriangles = new ArrayList<>();
            //var triangles =getTriangles(builder);
            for(TriangleGraph triangle : triangleGraphs) {
                if (GeometryUtility.isInsideCircleD(triangle.k.v1(),triangle.j.v1(),triangle.i.v1(),point)) {
                    badTriangles.add(triangle);
                }
            }
            ArrayList<Edge> edgesList = new ArrayList<>();
            for(TriangleGraph badTriangle : badTriangles) {

                Edge[] edges = new Edge[]{badTriangle.i,badTriangle.j,badTriangle.k};
                for(Edge edge : edges) {
                    boolean shared = triangleListContainsEdge(badTriangle, edge, badTriangles);
                    if(!shared) {
                        edgesList.add(edge);
                    }
                }
            }
            for(TriangleGraph triangle : badTriangles) {
                removeTriangle(builder,triangle);
                triangleGraphs.remove(triangle);

            }

            Vertex newV = new Vertex(point.x,point.y);
            for(Edge edge : edgesList) {
                // Create a triangle
                TriangleGraph triangleGraph = new TriangleGraph();
                triangleGraph.i = new Edge(edge.v1(),newV);
                triangleGraph.j = new Edge(newV,edge.v2());
                triangleGraph.k = new Edge(edge.v2(),edge.v1());
                triangleGraphs.add(triangleGraph);
                addOneTriangleInside(builder,edge,newV);

            }
            //builder.showGraph();
           /* try {
                //new CountDownLatch(3).await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

        }
        //var triangles = getTriangles(builder);
        for(TriangleGraph t : new ArrayList<>(triangleGraphs)) {
            ArrayList<Vertex> vertices = new ArrayList<>();
            vertices.add(t.i.v1());
            vertices.add(t.j.v1());
            vertices.add(t.k.v1());
            if(vertices.stream().anyMatch(v -> v.equals(new Vertex(0,100))
                    || v.equals(new Vertex(100,-100))
                    || v.equals(new Vertex(-100,-100)))) {
                //removeTriangle(builder,t);
                triangleGraphs.remove(t);
            }
        }
        builder.removeVertex(new Vertex(0,100));
        builder.removeVertex(new Vertex(100,-100));
        builder.removeVertex(new Vertex(-100,-100));
        builder.showGraph();
        try {
            new CountDownLatch(1).await(2,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return triangleGraphs;
    }

    public static ArrayList<TriangleGraph> getTriangles(Graph2DTopologyBuilder builder) {
        var cycles = builder.getCyclesListLarge(builder.getFirstVertex());
        ArrayList<TriangleGraph> triangles = new ArrayList<>();
        for(Graph2DTopologyBuilder.Cycle cycle : cycles) {
            Graph2DTopologyBuilder.Cycle c = cycle;
            if(!Graph2DTopologyBuilder.isCycleCCW(c)) {
                c = Graph2DTopologyBuilder.transformCycleToCCW(c);
            }
            if(c.cyclePath.size() -1 == 3) {
                TriangleGraph t = new TriangleGraph();
                t.i = builder.getEdge(c.cyclePath.get(0),c.cyclePath.get(1));
                t.j = builder.getEdge(c.cyclePath.get(1),c.cyclePath.get(2));
                t.k = builder.getEdge(c.cyclePath.get(2),c.cyclePath.get(0));
                triangles.add(t);
            }
        }
        return triangles;
    }

    public static boolean triangleListContainsEdge(TriangleGraph badTriangle, Edge edge, ArrayList<TriangleGraph> badTriangles) {
        boolean shared = false;
        for(TriangleGraph t : badTriangles) {
            if (t != badTriangle && (t.i.equals(edge) || t.j.equals(edge) || t.k.equals(edge))) {
                // t contains edge ?
                shared = true;
                break;
            }
        }
        return shared;
    }

    public static void removeTriangle(Graph2DTopologyBuilder builder, TriangleGraph t) {
        Edge e1 = t.i;
        Edge e2 = t.j;
        Edge e3 = t.k;

        builder.removeEdge(e1.v1(),e1.v2());
        builder.removeEdge(e2.v1(),e2.v2());
        builder.removeEdge(e3.v1(),e3.v2());
    }

    public static void addOneTriangleInside(Graph2DTopologyBuilder builder,Edge edge, Vertex newPoint) {
        if(!builder.hasEdge(edge.v1(),newPoint)) {
            builder.addEdge(edge.v1(),newPoint);
        }

        if(!builder.hasEdge(edge.v1(),edge.v2())) {
            builder.addEdge(edge.v1(),edge.v2());
        }
        if(!builder.hasEdge(edge.v2(),newPoint)) {
            builder.addEdge(edge.v2(),newPoint);
        }
    }

    public static void addTriangle(Graph2DTopologyBuilder builder, Edge edge, Vertex point) {
        new utility.graph.Vertex(point.x,point.y);
        builder.exists(new utility.graph.Vertex(point.x,point.y));
    }
}
