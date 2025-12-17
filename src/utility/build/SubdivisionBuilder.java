package utility.build;

import utility.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Only work with convex polygons
 */
public class SubdivisionBuilder {

    HashMap<Vertex, ArrayList<HalfEdge>> vertices ;
    HashMap<Face, ArrayList<HalfEdge>> faces;
    ArrayList<HalfEdge> cycles;

    private boolean edgeToInsertIsWrongDirection;

    SubdivisionBuilder() {
        vertices = new HashMap<>();
        faces = new HashMap<>();
        cycles = new ArrayList<>();
        edgeToInsertIsWrongDirection = false;
    }


    public static SubdivisionBuilder builder() {
        return new SubdivisionBuilder();
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
            if(GeometryUtility.toTheLeftOf(e1.v,e1.twin.v,e2.v) < 0) {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.connectEdgesCW(e1,e2);
            }
            return EdgeUtility.connectEdges(e1,e2);
        } else {

            var newEdge = EdgeUtility.createEdgeFromVertices(e1.twin.v,e2.v);
            if(GeometryUtility.toTheLeftOf(e1.v,e1.twin.v,e2.v) < 0) {
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

    public void connect(ArrayList<HalfEdge> edges,ArrayList<HalfEdge> edges2) {
        throw new RuntimeException("Not implemented");
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
        if(exists(v)) {
            connect(vertices.get(v),vertices.get(v2));
        }
        var listV2 = vertices.get(v2);
        if(listV2.size() == 1) {
            var newEdge = EdgeUtility.createEdgeFromVertices(v2,v);
            var edge = listV2.get(listV2.size()-1).twin;
            if(GeometryUtility.toTheLeftOf(edge.v,edge.twin.v,v) > 0) {
                return EdgeUtility.connectVertex(listV2.get(0).twin,v);
            } else {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.connectVertexCW(listV2.get(0).twin,v);
            }

        } else {
            var t0 = listV2.get(0).twin.prev;
            var newEdge = EdgeUtility.createEdgeFromVertices(v2,v);

            if(GeometryUtility.toTheLeftOf(t0.v,t0.twin.v,v) <= 0) {
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
            if(goToTheWrongDirection(v,v2)) {
                edgeToInsertIsWrongDirection = true;
                return EdgeUtility.createEdgeFromVertices(v2,v);
            }
            return EdgeUtility.createEdgeFromVertices(v,v2);

        } else if((exists(v2))){
            var listV2 = vertices.get(v2);
            var listV1 = vertices.get(v);
            if(listV2.size() == 1 && listV1.size() == 1) {

                return connectEdges( listV1.get(0).twin,listV2.get(0));


            } else if(listV2.size() > 1 && listV1.size() > 1) {
                connect(listV1,listV2);

            } else if(listV2.size() > 1) {
                return connectEdges(listV1.get(0).twin,listV2.get(0));
            }
        } else {
            return connect(v2,v);
        }
        return null;
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
        if(list == null) return false;
        return list.stream().anyMatch(edge -> edge.twin.v.equals(v2));
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
    public Face buildPolygon(ArrayList<Vertex> vertices) {
        ArrayList<HalfEdge> halfEdges = new ArrayList<>();
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
        return f;
    }
}
