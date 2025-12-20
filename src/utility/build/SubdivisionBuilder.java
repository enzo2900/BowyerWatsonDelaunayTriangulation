package utility.build;

import utility.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Only work with convex polygons
 */
public class SubdivisionBuilder {

    public HashMap<Vertex, ArrayList<HalfEdge>> vertices ;
    public HashMap<Face, ArrayList<HalfEdge>> faces;
    ArrayList<HalfEdge> cycles;

    private boolean edgeToInsertIsWrongDirection;

    private boolean insertingAPolygonCCW;
    SubdivisionBuilder() {
        vertices = new HashMap<>();
        faces = new HashMap<>();
        cycles = new ArrayList<>();
        edgeToInsertIsWrongDirection = false;
    }


    public static SubdivisionBuilder builder() {
        return new SubdivisionBuilder();
    }


    /**
     * Merge two adjacents faces from the edge to remove.
     * @param f
     * @param f2
     * @param toRemove
     * @param twin
     * @return the new face merged
     */
    public Face mergeFaces(Face f, Face f2, HalfEdge toRemove,HalfEdge twin) {
        vertices.get(toRemove.v).remove(toRemove);
        vertices.get(twin.v).remove(twin);

        HalfEdge e = toRemove.prev;
        HalfEdge e2 = twin.next;
        HalfEdge e3 = toRemove.next;
        HalfEdge e4 = twin.prev;

        HalfEdge eNext = twin.next;
        HalfEdge e2Prev = toRemove.prev;
        HalfEdge e3Prev = twin.prev;
        HalfEdge e4Next = toRemove.next;

        e.next = eNext;
        e2.prev = e2Prev;
        e3.prev = e3Prev;
        e4.next = e4Next;

        var list = getConnectedEdges(e);
        faces.put(f,list);
        faces.remove(f2);

        return f;
    }

    public Couple<HalfEdge,HalfEdge> splitFace(Face f, HalfEdge e1, HalfEdge e2) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();

        edge.v = e2.v;
        edgeTwin.v = e1.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        HalfEdge eNext = e1;
        HalfEdge ePrev = e2.prev;
        HalfEdge eTnext = e2;
        HalfEdge eTprev = e1.prev;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.prev = eTprev;
        edgeTwin.next = eTnext;

        eNext.prev = edge;
        ePrev.next = edge;
        eTnext.prev = edgeTwin;
        eTprev.next = edgeTwin;

        ArrayList<HalfEdge> edges = getConnectedEdges(edge);

        faces.put(f,edges);
        edges.forEach(edge1 -> edge1.incidentFace = f);
        Face f2 = new Face();
        ArrayList<HalfEdge> edges2 = getConnectedEdges(edgeTwin);
        faces.put(f2,edges2);
        edges2.forEach(edge1 -> edge1.incidentFace = f2);
        vertices.get(edge.v).add(edge);
        vertices.get(edgeTwin.v).add(edgeTwin);

        return new Couple<>(edge,edgeTwin);

    }

    public void swapEdgeFace(HalfEdge toSwap, Face f, Face f2) {
        HalfEdge opposite = toSwap.next.next;
        HalfEdge opposite2 = toSwap.twin.next.next;

        HalfEdge eNext = opposite;
        HalfEdge ePrev = opposite2.prev;
        HalfEdge eTNext = opposite2;
        HalfEdge eTPrev = opposite.prev;

        toSwap.next = eNext;
        toSwap.prev = ePrev;
        HalfEdge twin = toSwap.twin;

        twin.prev = eTPrev;
        twin.next = eTNext;

        eNext.prev = toSwap;
        ePrev.next = toSwap;
        eTPrev.next = twin;
        eTNext.prev = twin;

        ArrayList<HalfEdge> edges = getConnectedEdges(toSwap);
        faces.put(f,edges);
        ArrayList<HalfEdge> edges2 = getConnectedEdges(toSwap.twin);
        faces.put(f2,edges2);
    }

    public ArrayList<HalfEdge> getConnectedEdges(HalfEdge edge) {
        ArrayList<HalfEdge> halfEdgesFound = new ArrayList<>();
        HalfEdge first = edge;
        halfEdgesFound.add(first);

        HalfEdge next = edge.next;
        while (next != first) {
            halfEdgesFound.add(next);
            next = next.next;

        }
        //halfEdgesFound.add(first);
        return halfEdgesFound;
    }

    public ArrayList<HalfEdge> get(Vertex v) {
        return vertices.get(v);
    }
    public ArrayList<HalfEdge> getAdjacents(HalfEdge edge) {
        return EdgeUtility.getAdjacentsEdges(edge);
    }
    public Couple<HalfEdge,HalfEdge> connectEdges(HalfEdge e1, HalfEdge e2) {
        var listV2 = getAdjacents(e2);
        if(listV2.size() == 1) {
            if(GeometryUtility.toTheLeftOf(e1.v,e1.twin.v,e2.v) < 0 && !insertingAPolygonCCW) {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.connectEdgesCW(e1,e2);
            }
            return EdgeUtility.connectEdges(e1,e2);
        } else {

            var newEdge = EdgeUtility.createEdgeFromVertices(e1.twin.v,e2.v);
            if(GeometryUtility.toTheLeftOf(e1.v,e1.twin.v,e2.v) < 0 && !insertingAPolygonCCW) {
                // Wrong direction
                edgeToInsertIsWrongDirection = true;
                var lf = EdgeUtility.getLeftAndRightOf(newEdge.right(),listV2);
                HalfEdge leftConnection = lf.left();
                HalfEdge rightConnection = lf.right();
                return EdgeUtility.connectEdgeToEdgesCW(e1,rightConnection,leftConnection);
            }
            var lf = EdgeUtility.getLeftAndRightOf(newEdge.right(),listV2);
            HalfEdge leftConnection = GeometryUtility.toTheLeftOf(e2.v,e2.twin.v,e1.twin.v) > 0 ? lf.left().twin : lf.right().twin;
            HalfEdge rightConnection = GeometryUtility.toTheLeftOf(e2.v,e2.twin.v,e1.twin.v) > 0 ? lf.right() : lf.left();
            leftConnection = lf.left().twin;
            rightConnection = lf.right();
            return EdgeUtility.connectEdgeToEdges2(e1,rightConnection,leftConnection);
            //return EdgeUtility.connectEdgeToEdges(e1,rightConnection,leftConnection);
        }
    }

    /**
     *
     * @param edges A list with more than 1 elements
     * @param edges2 A list with more than 1 elements
     * @return an CCW edge and the twin CW edge
     */
    public Couple<HalfEdge,HalfEdge> connect(ArrayList<HalfEdge> edges,ArrayList<HalfEdge> edges2) {
        //throw new RuntimeException("Not implemented");
        var edge = edges.get(0);
        var v1 = edges.get(0).v;
        var v2 = edges2.get(0).v;
        //TODO detect if the edge is in wrong order
        if(GeometryUtility.toTheLeftOf(edge.twin.v,edge.v,v2) > 0 &&!insertingAPolygonCCW) {
            edgeToInsertIsWrongDirection = true;
            var edge1 = EdgeUtility.createEdgeFromVertices(v1,v2);
            var edge2 = EdgeUtility.createEdgeFromVertices(v2,v1);
            var lf1 = EdgeUtility.getLeftAndRightOf(edge1.left(),edges);
            var lf2 = EdgeUtility.getLeftAndRightOf(edge2.left(),edges2);
            return EdgeUtility.connectEdgesToEdgesCW(lf1.left().twin,lf1.right(),
                    lf2.left().twin,lf2.right());
        }
        var edge1 = EdgeUtility.createEdgeFromVertices(v1,v2);
        var edge2 = EdgeUtility.createEdgeFromVertices(v2,v1);
        var lf1 = EdgeUtility.getLeftAndRightOf(edge1.left(),edges);
        var lf2 = EdgeUtility.getLeftAndRightOf(edge2.left(),edges2);
        return EdgeUtility.connectEdgesToEdges(lf1.left().twin,lf1.right(),lf2.left().twin,lf2.right());
    }

    public static boolean goToTheWrongDirection(Vertex v, Vertex v2) {
        double x = v2.x - v.x;
        double y = v2.y - v.y;
        return x< 0 || y < 0;
    }
    public boolean exists(Vertex v) {
        return vertices.containsKey(v);
    }

    public Couple<HalfEdge,HalfEdge> connect(Vertex v, Vertex v2) {
        /*if(exists(v)) {
            connect(vertices.get(v),vertices.get(v2));
        }*/
        var listV2 = vertices.get(v2);
        if(listV2 == null) {
            var listV1 = vertices.get(v);
            if(listV1.size() ==1) {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.connectVertexCW(listV1.get(0).twin,v);
            } else {
                var newEdge = EdgeUtility.createEdgeFromVertices(v,v2);
                edgeToInsertIsWrongDirection = true;
                var lf = EdgeUtility.getLeftAndRightOf(newEdge.left(),listV1);
                HalfEdge BeforeConnection = GeometryUtility.toTheLeftOf(v,lf.right().twin.v,v2) > 0 ? lf.left().twin : lf.right().twin;
                HalfEdge AfterConnection = GeometryUtility.toTheLeftOf(v,lf.right().twin.v,v2) > 0 ? lf.right() : lf.left();
                return EdgeUtility.connectVertexToBothEdgeCW(BeforeConnection,AfterConnection,v2);
            }


        }
        if(listV2.size() == 1) {
            var newEdge = EdgeUtility.createEdgeFromVertices(v2,v);
            var edge = listV2.get(listV2.size()-1).twin;
            if(GeometryUtility.toTheLeftOf(edge.v,edge.twin.v,v) > 0 || insertingAPolygonCCW) {
                return EdgeUtility.connectVertex(listV2.get(0).twin,v);
            } else {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.connectVertexCW(listV2.get(0).twin,v);
            }

        } else {
            var t0 = listV2.get(0).twin.prev;
            var newEdge = EdgeUtility.createEdgeFromVertices(v2,v);

            if(GeometryUtility.toTheLeftOf(t0.v,t0.twin.v,v) <= 0 && !insertingAPolygonCCW) {
                edgeToInsertIsWrongDirection = true;
                var lf = EdgeUtility.getLeftAndRightOf(newEdge.left(),listV2);
                HalfEdge BeforeConnection = GeometryUtility.toTheLeftOf(v2,lf.right().twin.v,v) > 0 ? lf.left().twin : lf.right().twin;
                HalfEdge AfterConnection = GeometryUtility.toTheLeftOf(v2,lf.right().twin.v,v) > 0 ? lf.right() : lf.left();
                return EdgeUtility.connectVertexToBothEdgeCW(BeforeConnection,AfterConnection,v);
            }
            var lf = EdgeUtility.getLeftAndRightOf(newEdge.left(),listV2);

            HalfEdge BeforeConnection = GeometryUtility.toTheLeftOf(v2,lf.right().twin.v,v) > 0 ? lf.left().twin : lf.right().twin;
            HalfEdge AfterConnection = GeometryUtility.toTheLeftOf(v2,lf.right().twin.v,v) > 0 ? lf.right() : lf.left();
            BeforeConnection = lf.left().twin;
            AfterConnection = lf.right();
            return EdgeUtility.connectVertexToBothEdge2(BeforeConnection,AfterConnection,v);
            //return EdgeUtility.connectVertexToBothEdge(BeforeConnection,AfterConnection,v);
        }

    }

    public SubdivisionBuilder buildVertex(Vertex v,Vertex v2) {
        edgeToInsertIsWrongDirection = false;
        var couple = build(v,v2);

        var listV1 = vertices.getOrDefault(v,new ArrayList<>());
        var listV2 = vertices.getOrDefault(v2,new ArrayList<>());
        if(edgeToInsertIsWrongDirection) {
            listV2.add(couple.left());
            listV1.add(couple.right());
            vertices.put(v,listV1);
            vertices.put(v2,listV2);
        } else {
            listV1.add(couple.left());
            vertices.put(v,listV1);
            listV2.add(couple.right());
            vertices.put(couple.right().v,listV2);
        }

        return this;
    }

    public Couple<HalfEdge,HalfEdge> build(Vertex v,Vertex v2) {
        if(!exists(v) && !exists(v2)) {
            //if(goToTheWrongDirection(v,v2)) {
              //  edgeToInsertIsWrongDirection = true;
               // return EdgeUtility.createEdgeFromVertices(v2,v);
            //}
            return EdgeUtility.createEdgeFromVertices(v,v2);

        } else if((exists(v2))){
            var listV2 = vertices.get(v2);
            var listV1 = vertices.get(v);
            if(listV1 == null) {
                return connect(v2,v);
            }
            if(listV2.size() == 1 && listV1.size() == 1) {

                return connectEdges( listV1.get(0).twin,listV2.get(0));


            } else if(listV2.size() > 1 && listV1.size() > 1) {
                return connect(listV1,listV2);

            } else if(listV2.size() > 1) {
                return connectEdges(listV1.get(0).twin,listV2.get(0));
            }else {
                HalfEdge e1 = listV1.get(0);
                HalfEdge e2 = listV2.get(0);
                var newEdge = EdgeUtility.createEdgeFromVertices(v,v2);

                    // Wrong direction
                edgeToInsertIsWrongDirection = true;
                var lf = EdgeUtility.getLeftAndRightOf(newEdge.left(),listV1);
                HalfEdge leftConnection = lf.left().twin;
                HalfEdge rightConnection = lf.right();
                return EdgeUtility.connectEdgeToEs(e2,rightConnection,leftConnection);
            }
        } else {
            return connect(v2,v);
        }
    }

    public Couple<HalfEdge,HalfEdge> buildV(Vertex v, Vertex v2) {
        edgeToInsertIsWrongDirection = false;
        var couple = build(v,v2);

        var listV1 = vertices.getOrDefault(couple.left().v,new ArrayList<>());
        var listV2 = vertices.getOrDefault(couple.right().v,new ArrayList<>());
        if(edgeToInsertIsWrongDirection) {
            listV1.add(couple.left());
            listV2.add(couple.right());
            vertices.put(couple.left().v,listV1);
            vertices.put(couple.right().v,listV2);
        } else {
            listV1.add(couple.left());
            vertices.put(couple.left().v,listV1);
            listV2.add(couple.right());
            vertices.put(couple.right().v,listV2);
        }

        return couple;
    }

    public boolean edgeExists(Vertex v, Vertex v2) {
        var list = vertices.get(v);
        var list2 = vertices.get(v2);
        if(list == null && list2 == null) return false;

        if(list != null) {
            return list.stream().anyMatch(edge -> edge.twin.v.equals(v2));
        }
        return list2.stream().anyMatch(edge -> edge.twin.v.equals(v)  );
    }

    public HalfEdge getEdge(Vertex v, Vertex v2) {
        var list = vertices.get(v);
        return list.stream().filter(edge -> edge.twin.v.equals(v2)).toList().get(0);
    }
    /**
     *
     * @param vertices
     * @return return the face of the polygon
     */
    public Face buildPolygon(ArrayList<Vertex> verticesList) {
        ArrayList<HalfEdge> halfEdges = new ArrayList<>();
        List<Vertex> vertices ;
        if(isCWPolygon(verticesList)) {
            vertices = verticesList.reversed();
        }else {
            vertices = verticesList;
        }
        insertingAPolygonCCW = true;
        Face f = new Face();
        for(int i = 0 ; i < vertices.size()-1 ; i ++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get(i+1);
            if(edgeExists(v1,v2)) {

                var edge = getEdge(v1,v2);
                if(edge.incidentFace != null) {
                    edge = edge.twin;
                }

                //HalfEdge TNext = halfEdges.get(i-1);
                //edge.next = TNext;
                halfEdges.add(edge);
                halfEdges.add(edge.twin);
                edge.incidentFace = f;
                continue;
            }
            var couple = buildV(vertices.get(i),vertices.get(i+1));
            halfEdges.add(couple.left());
            halfEdges.add(couple.right());
            couple.left().incidentFace = f;
        }
        f.outerComponent = halfEdges.get(0);
        faces.put(f,halfEdges);
        insertingAPolygonCCW  =false;
        return f;
    }

    public void removePolygon(Face f) {

    }

    public Triangle toTriangle(Face f) {
        var list = faces.get(f);
        if(list.size() == 6) {
            Triangle t = new Triangle(list.get(0).v,list.get(2).v,list.get(4).v);
            t.edges.add(list.get(0));
            t.edges.add(list.get(2));
            t.edges.add(list.get(4));
            t.f = f;
            return t;
        }
        return null;
    }

    public ArrayList<Triangle> getTriangles() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        for(Face f : faces.keySet()) {
            var list = faces.get(f);
            if(list.size() == 6) {
                Triangle t = new Triangle(list.get(0).v,list.get(2).v,list.get(4).v);
                t.edges.add(list.get(0));
                t.edges.add(list.get(2));
                t.edges.add(list.get(4));
                t.f = f;
                triangles.add(t);
            }
        }
        return triangles;
    }

    public static boolean isCWPolygon(List<Vertex> vertices) {
        double sum = 0;
        for(int i = 0 ; i < vertices.size()-1; i++) {
            Vertex v1 = vertices.get(i);
            Vertex v2 = vertices.get(i+1);
            sum += (v1.x * v2.y) - (v2.x * v1.y);
        }
        return sum < 0;
    }

}
