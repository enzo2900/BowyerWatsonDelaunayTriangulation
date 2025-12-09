package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Polygon {

    public Face f;

    public ArrayList<Vertex> vertices;

    public ArrayList<HalfEdge> halfEdges;

    public Polygon() {
        vertices = new ArrayList<>();
        halfEdges = new ArrayList<>();
    }

    public Polygon copy() {
        Polygon polygon = new Polygon();
        ArrayList<Vertex> verticesToCopy = new ArrayList<>();
        ArrayList<HalfEdge> edges1 = new ArrayList<>();
        Map<HalfEdge, HalfEdge> copyMap = new HashMap<>();
        Map<Face,Face> faceFaceMap = new HashMap<>();
        Map<Vertex,Vertex> vertexMap = new HashMap<>();

        for(HalfEdge e : halfEdges) {
            HalfEdge copy = new HalfEdge();
            copyMap.put(e, copy);

        }
        for(Vertex v: vertices) {
            Vertex vertexCopied = new Vertex(v.x,v.y);
            vertexMap.put(v,vertexCopied);
            vertexCopied.incidentEdge = copyMap.get(v.incidentEdge);
            verticesToCopy.add(vertexCopied);
        }

        Face f2=  f;
        Face fCopied = new Face();
        faceFaceMap.put(f2,fCopied);
        polygon.f = f2;

        fCopied.outerComponent = copyMap.get(f2.outerComponent);
        for(HalfEdge e : f2.innerComponents) {
            fCopied.innerComponents.add(copyMap.get(e));
        }


        for(HalfEdge e : halfEdges) {
            HalfEdge copy = copyMap.get(e);

            Vertex vCopy = vertexMap.get(e.v);
            copy.v = vCopy; // ou copier le vertex si nécessaire

            copy.incidentFace = faceFaceMap.get(e.incidentFace);; // ou créer une copie de la face
            copy.next = copyMap.get(e.next);
            copy.prev = copyMap.get(e.prev);
            copy.twin = copyMap.get(e.twin);
            copy.tag = e.tag;

            edges1.add(copy);
        }

        return polygon;
    }

    public Subdivision toSubdivision() {
        Subdivision subdivision = new Subdivision();
        subdivision.faces.add(f);
        subdivision.halfEdges.addAll(halfEdges);
        subdivision.vertices.addAll(vertices);
        for(Vertex v: vertices) {
            ArrayList<Face> faces = new ArrayList<>();
            faces.add(f);
            subdivision.vertexMap.put(v,faces);
        }
        return subdivision;
    }
}
