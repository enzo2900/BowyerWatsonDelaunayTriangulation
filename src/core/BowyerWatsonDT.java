package core;

import utility.*;

import java.util.ArrayList;

/**
 * Une implémentation plus simpliste que l'approche incrémentale cité dans le livre Computational Géometry.
 * Utiliser l'article pour l'implémenter, une recherche plus approfondie peut être nécessaire.
 * <a href="https://en.wikipedia.org/wiki/Bowyer%E2%80%93Watson_algorithm">Watson</a>
 */
public class BowyerWatsonDT {

    public void compute(ArrayList<Point> points) {

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
                    for(Triangle t : badTriangles) {
                        if (t != badTriangle) {
                            // t contains edge ?
                        }
                    }
                }
            }
            for(Triangle triangle : badTriangles) {
                builder.removeVertex(triangle.i);
                builder.removeVertex(triangle.j);
                builder.removeVertex(triangle.k);

            }

            for(HalfEdge edge : polygon.halfEdges) {
                // Create a triangle

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

            }
        }
    }
    public static void removeTriangle(Subdivision D, Triangle t) {

        // Récupérer les 3 half-edges du triangle
        HalfEdge e1 = t.i.incidentEdge;
        HalfEdge e2 = t.j.incidentEdge;
        HalfEdge e3 = t.k.incidentEdge;

        // Supprimer les faces
        var triangles = D.getTriangles();
        triangles.remove(t);
        HalfEdge.removeEdge(D,e1.v,e1.next.v);
        HalfEdge.removeEdge(D,e2.v,e2.next.v);
        HalfEdge.removeEdge(D,e3.v,e3.next.v);
    }
}
