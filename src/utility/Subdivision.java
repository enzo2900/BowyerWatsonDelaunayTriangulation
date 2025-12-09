package utility;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Subdivision {

    public ArrayList<Vertex> vertices;

    public ArrayList<HalfEdge> halfEdges;

    public ArrayList<Face> faces;

    public int tag;

    public HashMap<Vertex,ArrayList<Face>> toComplete;

    public HashMap<Vertex,ArrayList<Face>> vertexMap;

    public Subdivision() {
        vertices = new ArrayList<>();
        halfEdges = new ArrayList<>();
        faces = new ArrayList<>();
        vertexMap = new HashMap<>();
        toComplete = new HashMap<>();
    }
    public boolean isVertexInFace(Face f, Vertex v) {
        HalfEdge e = f.outerComponent;
        double theLeftOf = GeometryUtility.toTheLeftOf(e.v,e.next.v,v);
        if(theLeftOf < 0) {
            return false;
        }
        HalfEdge currnet = e.next;
        while (currnet != e) {
            double toTheRight2 = GeometryUtility.toTheLeftOf(currnet.v,currnet.next.v,v);
            if(toTheRight2 < 0) {
                return false;
            }
            currnet = currnet.next;
        }
        return true;
    }

    public ArrayList<Vertex> findWhere(Face f) {
        ArrayList<Vertex> vertices1 = new ArrayList<>();
        for(Vertex vertex : vertices) {
            if(vertexMap.containsKey(vertex)) {
                var map = vertexMap.get(vertex);
                if(map.contains(f)) {
                    vertices1.add(vertex);
                }

            }
        }
        return vertices1;
    }

    public void removeUneccessaryFace() {
        ArrayList<Face> toRemove = new ArrayList<>();
        for(Face f : faces) {
            if(!hasVertex(f.outerComponent.v) || !hasEdge(f.outerComponent.v,f.outerComponent.twin.v)) {
                toRemove.add(f);
            }
        }
        for(Face f : toRemove) {
            var list = findWhere(f);
            for(Vertex v : list) {
                var map  = vertexMap.get(v);
                map.remove(f);
            }
        }
        faces.removeAll(toRemove);
    }

    public ArrayList<HalfEdge> findAllFrom(Face f) {
        ArrayList<HalfEdge> edges = new ArrayList<>();
        HalfEdge edge = f.outerComponent;
        HalfEdge current = edge.next;

        ArrayList<HalfEdge> visited = new ArrayList<>();
        visited.add(edge);
        visited.add(edge.twin);
        edges.add(edge);
        edges.add(edge.twin);
        while (current != edge){
            if(visited.contains(current)) {
                System.out.println("Incorrect face topology.");
                break;
            }
            visited.add(current);
            visited.add(current.twin);
            edges.add(current);
            edges.add(current.twin);
            current = current.next;

        }
        return edges;
    }

    public ArrayList<Triangle> getTriangles() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        for(Face f : faces) {
            if(hasVertex(f.outerComponent.v)) {
                var listEdges = findAllFrom(f);

                if( listEdges.size() == 6) {
                    var t = new Triangle(listEdges.get(0).v,listEdges.get(2).v,listEdges.get(4).v);
                    t.edges = listEdges;
                    triangles.add(t);
                }
            } else {
                System.out.println("Face with non existing Vertex.");
            }
        }
        return triangles;
    }

    public ArrayList<HalfEdge> adjacentsEdgesTo(Vertex v) {
        ArrayList<HalfEdge> edges = new ArrayList<>();
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge first = v.incidentEdge;

        if(first == null || !hasEdge(first.v,first.twin.v)) {
            return edges;
        }
        visited.add(first);
        visited.add(first.twin);
        edges.add(first);
        HalfEdge current = first.twin.next;
        while (current != first) {
            if(!visited.contains(current) && ! visited.contains(current.twin)) {
                visited.add(current);
                visited.add(current.twin);

                edges.add(current);
            }


            current = current.twin.next;
        }
        return edges;
    }

    public void removeVertexIfNecessary(Vertex v) {
        if(v.incidentEdge == null) {
            vertices.remove(v);
        }
    }
    public ArrayList<Vertex> findAllWhere(Face f) {
        ArrayList<Vertex> vertices1 = new ArrayList<>();
        for(Vertex vertex : vertices) {
            if(toComplete.containsKey(vertex)) {
                var map = toComplete.get(vertex);
                if(map.contains(f)) {
                    vertices1.add(vertex);
                }

            }
        }
        return vertices1;
    }

    public HalfEdge getEdge(Vertex from, Vertex to) {
        for(HalfEdge edge: halfEdges) {
            if(edge.v == null) continue;
            if(edge.v.equals(from) && edge.twin.v.equals(to)) {
                return edge;
            }
        }
        return null;
    }
    public Subdivision copy() {
        ArrayList<Vertex> copiedVertices = new ArrayList<>();

        ArrayList<Face> copiedFaces = new ArrayList<>();
        HashMap<Vertex,ArrayList<Face>> faceMap = new HashMap<>();
        if(!Subdivision.inEdgeBounded(halfEdges)) {
            throw new RuntimeException("Cant copy if the subdivision has no proper in edge bound");
        }
        if(!Subdivision.outerEdgeBounded(halfEdges)) {
            throw new RuntimeException("Cant copy if the subdivision has no proper out edge bound");
        }

        ArrayList<HalfEdge> copiedHalfEdges = HalfEdge.copyStatic(halfEdges,faces,copiedFaces,vertices,copiedVertices,this,faceMap);


        Subdivision s = new Subdivision();
        s.vertices = copiedVertices;
        s.faces = copiedFaces;
        s.halfEdges = copiedHalfEdges;
        s.vertexMap = faceMap;
        s.vertexMap = faceMap;
        return s;
    }


    public static boolean equalsStatic(Subdivision D1,Subdivision D2) {
        for(Vertex v : D1.vertices) {

            if(!D2.vertices.contains(v)) {
                return false;
            }

        }

        for(HalfEdge e : D1.halfEdges) {

            boolean contains = false;
            for(HalfEdge e2 : D2.halfEdges) {
                if(e2.v.equals(e.v) && e.twin.v.equals(e2.twin.v)) {
                    contains = true;
                }
            }
            if(!contains) {
                return false;
            }

        }

        return D1.halfEdges.size() == D2.halfEdges.size()
                && D1.vertices.size()== D2.vertices.size();

    }

    public boolean hasEdge(Vertex u, Vertex to) {
        for(HalfEdge edge: halfEdges) {
            if(edge.v == null) continue;
            if(edge.v.equals(u) && edge.twin.v.equals(to) || edge.twin.v.equals(u) && edge.v.equals(to)) {
                return true;
            }
        }
        return false;

    }

    public ArrayList<HalfEdge> getEdgesAdjacent(Vertex v) {
        ArrayList<HalfEdge> edges = new ArrayList<>();
        for(HalfEdge edge : halfEdges) {
            if(edge.v.equals(v)) {
                edges.add(edge);
                edges.add(edge.twin);
            }
        }
        return edges;
    }
    public void printEdges() {
        for(HalfEdge e : halfEdges) {
            System.out.println(" (" +e.v.x+","+e.v.y +") --> (" +e.twin.v.x +","+e.twin.v.y + ")" );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Subdivision s) {
            return s.halfEdges.size() == halfEdges.size() && s.faces.size() == faces.size() && s.vertices.size()==vertices.size();
        }
        return false;
    }

    /**
     * Creates new distinct polygons from the monotone Subdivision.
     * The outer edges are redefined to be the one from the polygon.
     * The polygons found are copy of the original Subdivision
     * @param D La subdivision monotone y.
     * @return Une liste de polygones distincts.
     */
    public static ArrayList<Polygon> findNewFacesFromDiagonals(Subdivision D) {

        Set<HalfEdge> vVisited =  new HashSet<>();
        ArrayList<Polygon> polygons = new ArrayList<>();
        Subdivision DCopied = D.copy();

        for(int i = 0 ; i < DCopied.halfEdges.size(); i ++) {
            HalfEdge v = DCopied.halfEdges.get(i);
            if(vVisited.contains(v)) {
                continue;
            }
            if(v.incidentFace == null) {
                continue;
            }
            vVisited.add(v);
            HalfEdge head = v;
            Polygon polygon = new Polygon();
            Set<Vertex> S = new HashSet<>();
            ArrayList<HalfEdge> A = new ArrayList<>();
            S.add(head.v);
            A.add(head);
            A.add(head.twin);
            vVisited.add(v.twin);
            Face f = new Face();
            polygon.f = f;
            head.incidentFace = f;
            //head.incidentEdge.twin.prev = head.incidentEdge.next.twin;
            //head.incidentEdge.twin.next = head.incidentEdge.prev.twin;
            f.outerComponent = head;
            HalfEdge current = head.next;
            while (current != head) {

                vVisited.add(current);
                vVisited.add(current.twin);

                S.add(current.v);
                //current.v.incidentEdge = HalfEdge.findFrom(A.getFirst(),current.v);
                A.add(current);
                A.add(current.twin);
                if(current.twin.next.v.equals(current.v)) {

                    if(current.twin.v == null) {
                        current.twin.v = current.prev.v;
                    }

                    //current.twin.prev = current.next.twin;
                    //current.twin.next = current.prev.twin;
                }

                current.incidentFace = f;

                current = current.next;
            }
            polygon.halfEdges = A;
            polygon.vertices = new ArrayList<>(S);
            polygons.add(polygon);
            DCopied = DCopied.copyNoCheck();
        }

        for(Polygon polygon : polygons) {
            for(HalfEdge edge : polygon.halfEdges) {
                if(edge.incidentFace == null) {
                    continue;
                }
                edge.twin.incidentFace = null;
                edge.twin.next = edge.prev.twin;
                edge.twin.prev = edge.next.twin;
            }
        }
        return polygons;
    }

    private Subdivision copyNoCheck() {
        ArrayList<Vertex> copiedVertices = new ArrayList<>();

        ArrayList<Face> copiedFaces = new ArrayList<>();
        HashMap<Vertex,ArrayList<Face>> faceMap = new HashMap<>();
        ArrayList<HalfEdge> copiedHalfEdges = HalfEdge.copyStatic(halfEdges,faces,copiedFaces,vertices,copiedVertices,this,faceMap);


        Subdivision s = new Subdivision();
        s.vertices = copiedVertices;
        s.vertexMap = faceMap;
        s.faces = copiedFaces;
        s.halfEdges = copiedHalfEdges;
        return s;
    }

    public static boolean alltwinandedgecorresponds(ArrayList<HalfEdge> edges){
        ArrayList<HalfEdge> copy = new ArrayList<>(edges);
        for (int i = 0 ; i < edges.size() ; i+=2) {
            HalfEdge edge = edges.get(i);
            if(!copy.contains(edge.twin)) {
                return false;
            }
            copy.remove(edge);
            copy.remove(edge.twin);

        }
        return true;


    }

    public static boolean outerEdgeBounded(ArrayList<HalfEdge> edges) {
        HalfEdge first = edges.stream().filter(e->  e.incidentFace == null).findFirst().get();

        if(first.incidentFace != null) {
            throw new RuntimeException("The algorithm is so bad it cant get a correct outer edge on the first try in outerEdgeBounded");
        }
        HalfEdge current = first.next;
        int counter = 0;
        int maxCounter = edges.size();
        while (current != first) {
            counter++;
            if(maxCounter <= counter) {
                throw new RuntimeException("OuterEdges are not bouded");
            }
            if(current.incidentFace != null) {
                throw new RuntimeException("Not an outer edge face");
            }
            if(!edges.contains(current)) {
                throw new RuntimeException("OuterEdge is not in the polygon. The polygon has not a correct outer bound.");
            }
            current = current.next;
        }
        return true;


    }

    public static boolean inEdgeBounded(ArrayList<HalfEdge> edges) {
        HalfEdge first =edges.getFirst();
        if(first.incidentFace == null) {
            throw new RuntimeException("The algorithm is so bad it cant get a correct in edge on the first try in inedgeBounded");
        }
        HalfEdge current = first.next;
        int counter = 0;
        int maxCounter = edges.size();
        while (current != first) {
            counter++;
            if(maxCounter <= counter) {
                throw new RuntimeException("OuterEdges are not bouded");
            }
            if(current.incidentFace == null) {
                throw new RuntimeException("Not an in edge face");
            }
            if(!edges.contains(current)) {
                throw new RuntimeException("inEdge is not in the polygon. The polygon has not a correct in bound.");
            }
            current = current.next;
        }
        return true;


    }

    public boolean hasVertex(Vertex v) {
        for(Vertex vertex : vertices) {
            if(v.equals(vertex)) {
                return true;
            }
        }
        return false;
    }

}
