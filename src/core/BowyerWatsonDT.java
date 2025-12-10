package core;

import utility.*;

import java.util.ArrayList;

/**
 * Une implémentation plus simpliste que l'approche incrémentale cité dans le livre Computational Géometry.
 * Utiliser l'article pour l'implémenter, une recherche plus approfondie peut être nécessaire.
 * <a href="https://en.wikipedia.org/wiki/Bowyer%E2%80%93Watson_algorithm">Watson</a>
 */
public class BowyerWatsonDT {

    public static void compute(ArrayList<Point> points) {

        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,1000))
                .Vertex(new Vertex(-1000,-1000))
                .Vertex(new Vertex(1000,-1000))
                .Vertex(new Vertex(0,1000));

        for(Point point : points) {
            ArrayList<Triangle> badTriangles = new ArrayList<>();
            var triangles = builder.D.getTriangles();
            for(Triangle triangle : triangles) {
                if (GeometryUtility.isInsideCircleD(triangle.i,triangle.j,triangle.k,point)) {
                    badTriangles.add(triangle);
                }
            }
            Polygon polygon =  new Polygon();
            for(Triangle badTriangle : badTriangles) {
                HalfEdge[] edges = new HalfEdge[]{badTriangle.i.incidentEdge,badTriangle.j.incidentEdge,badTriangle.k.incidentEdge};
                for(HalfEdge edge : edges) {
                    boolean shared = triangleListContainsEdge(badTriangle, edge, badTriangles);
                    if(!shared) {
                        polygon.halfEdges.add(edge);
                    }
                }
            }
            for(Triangle triangle : badTriangles) {
                removeTriangle(builder,triangle);

            }

            Vertex newV = new Vertex(point.x,point.y);
            for(HalfEdge edge : polygon.halfEdges) {
                // Create a triangle
                addOneTriangleInside(builder,edge,newV);

            }

        }
        var triangles = builder.D.getTriangles();
        for(Triangle t : triangles) {
            ArrayList<Vertex> vertices = new ArrayList<>();
            vertices.add(t.i);
            vertices.add(t.j);
            vertices.add(t.k);
            if(vertices.stream().anyMatch(v -> v.equals(new Vertex(0,1000))
                    || v.equals(new Vertex(1000,-1000))
                    || v.equals(new Vertex(-1000,-1000)))) {
                removeTriangle(builder,t);
            }
        }
        builder.D.removeVertexIfNecessary(builder.getVertex(new Vertex(0,1000)));
        builder.D.removeVertexIfNecessary(builder.getVertex(new Vertex(1000,-1000)));
        builder.D.removeVertexIfNecessary(builder.getVertex(new Vertex(-1000,-1000)));
    }

    public static boolean triangleListContainsEdge(Triangle badTriangle, HalfEdge edge, ArrayList<Triangle> badTriangles) {
        boolean shared = false;
        for(Triangle t : badTriangles) {
            if (t != badTriangle && t.edges.contains(edge)) {
                // t contains edge ?
                shared = true;
                break;
            }
        }
        return shared;
    }

    public static void removeTriangle(HalfEdge.SubdivisionBuilder D, Triangle t) {


        HalfEdge e1 = t.edges.get(0);
        HalfEdge e2 = t.edges.get(2);
        HalfEdge e3 = t.edges.get(4);

        D.removeHalfEdge(t.edges.get(0).v,e1.next.v);
        D.removeHalfEdge(e2.v,e2.next.v);
        D.removeHalfEdge(e3.v,e3.next.v);


        D.D.faces.remove(t.f);
        D.D.removeFace(t.f);
    }

    public static void addTriangleInsideTriangle(HalfEdge.SubdivisionBuilder D , Vertex v1, Vertex v2, Vertex v3, Vertex newPoint) {

        if(!D.exists(newPoint) ) {
            D.addIncompleteEdgeFrom(v1,newPoint);
        } else {
            D.addHalfEdge(v1,newPoint);
        }
        D.addHalfEdge(v2,newPoint);
        D.addHalfEdge(v3,newPoint);

    }

    public static void addOneTriangleInside(HalfEdge.SubdivisionBuilder D,HalfEdge edge, Vertex newPoint) {
        if(!D.exists(newPoint) ) {
            D.addIncompleteEdgeFrom(edge.v,newPoint);
        } else {
            if(!D.D.hasEdge(edge.v,newPoint))
                D.addHalfEdge(edge.v,newPoint);
        }
        if(!D.D.hasEdge(edge.v,edge.next.v)) {
            D.addHalfEdge(edge.v,edge.next.v);
        }
        if(!D.D.hasEdge(edge.next.v,newPoint)) {
            D.addHalfEdge(edge.next.v,newPoint);
        }

    }
}
