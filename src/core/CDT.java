package core;

import utility.*;
import utility.build.SubdivisionBuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;

public class CDT {

    public static SubdivisionBuilder compute(ArrayList<Point> points) {
        var triangles = BowyerWatsonDT2.compute(points);


        var builder = SubdivisionBuilder.builder();
        for(TriangleGraph triangleGraph : triangles) {
            ArrayList<Vertex> triangle = new ArrayList<>();
            var v=triangleGraph.i.v1();
            triangle.add(new Vertex(v.x,v.y));
            v=triangleGraph.j.v1();
            triangle.add(new Vertex(v.x,v.y));
            v=triangleGraph.k.v1();
            triangle.add(new Vertex(v.x,v.y));
            v=triangleGraph.i.v1();
            triangle.add(new Vertex(v.x,v.y));
            var f = builder.buildPolygon(triangle);
        }


        return builder;

    }

    public static boolean onSegments(MapPoint p, MapPoint q, MapPoint r) {
        return Math.min(p.getX(),r.getX()) < q.getX() &&  q.getX() < Math.max(p.getX(),r.getX())
                && Math.min(p.getY(),r.getY()) < q.getY() &&  q.getY() < Math.max(p.getY(),r.getY());
    }

    public static boolean xOverlap(MapPoint p, MapPoint q, MapPoint r, MapPoint t) {
        double rt = t.getX() - r.getX();
        double rtY = t.getY() - r.getY();
        double xParY = rt/rtY;

        double xp = xParY * (p.getY()-t.getY()) +t.getY();
        double xq = xParY * (q.getY() -t.getY()) + t.getY();
        return xp < p.getX() && xq > q.getX() || xp > p.getX() && xp < q.getX() || xq > p.getX() && xq < q.getX();
    }

    public static boolean yOverlap(MapPoint p, MapPoint q, MapPoint r, MapPoint t) {
        double rt = t.getX() - r.getX();
        double rtY = t.getY() - r.getY();
        double yParX = rtY/rt;

        double yP = yParX * (p.getX()-t.getX()) +t.getX();
        double yQ = yParX * (q.getX() -t.getX()) + t.getX();
        return yP < p.getY() && yQ > q.getY() || yP > p.getY() && yP < q.getY() || yQ > p.getY() && yQ < q.getY();
    }

    public static boolean intersects(MapPoint p, MapPoint q, MapPoint r, MapPoint t) {
        var o1 = GeometryUtility.toTheLeftOf(p,q,r);
        var o2 = GeometryUtility.toTheLeftOf(p,q,t);

        if(o1 * o2 < 0) {


            return xOverlap(p,q,r,t) && yOverlap(p,q,r,t);
        }

        if(o1== 0  && onSegments(p,r,q)) {
            return true;
        }

        if(o2 == 0 && onSegments(p,t,q)) {
            return true;
        }

        return false;
    }

    public static void applyConstraints(List<ConstraintPolygon> polygons,SubdivisionBuilder subdivision) {


        for(ConstraintPolygon polygon : polygons) {
            var segments = polygon.segments;
            for(Segment<Vertex> constraint : segments) {
                if(subdivision.edgeExists(constraint.start,constraint.end)) {
                    System.out.println("constraint segment " + constraint.start.x +" " +constraint.start.y + " " + constraint.end.x +" " + constraint.end.y+ " Exists. No contraint applied");
                    continue;
                }
                Queue<Face> facesToCheck = new ArrayDeque<>();
                var list = subdivision.vertices.get(constraint.start);
                for(HalfEdge hf : list) {
                    if(hf.incidentFace != null && !facesToCheck.contains(hf.incidentFace)) {
                        facesToCheck.add(hf.incidentFace);
                    }
                }
                ArrayList<HalfEdge> edgesToCheck = new ArrayList<>();
                while (!facesToCheck.isEmpty()) {
                    Face f = facesToCheck.poll();
                    var listE = subdivision.faces.get(f);
                    if(listE == null) {
                        System.err.println("Mauvaises faces construites.");
                        continue;
                    }
                    HalfEdge edgeI = listE.get(0);
                    HalfEdge edgeJ = listE.get(2);
                    HalfEdge edgeK = listE.get(4);



                    if(intersects(constraint.start,constraint.end,edgeI.v,edgeI.twin.v)) {
                        splitEdgeIfNecessary(subdivision, constraint, edgeI, f, facesToCheck, edgesToCheck);
                        continue;
                    }
                    if(intersects(constraint.start,constraint.end,edgeJ.v,edgeJ.twin.v)) {
                        splitEdgeIfNecessary(subdivision, constraint,edgeJ, f, facesToCheck, edgesToCheck);
                        continue;
                    }
                    if(intersects(constraint.start,constraint.end,edgeK.v,edgeK.twin.v)) {
                        splitEdgeIfNecessary(subdivision, constraint, edgeK, f, facesToCheck, edgesToCheck);
                    }
                }
                for(HalfEdge edge : edgesToCheck) {
                }
            }
        }


    }

    public static  void removePolygons(SubdivisionBuilder subdivision, List<ConstraintPolygon> polygons) {
        ArrayList<Vertex> constrainedVertices = new ArrayList<>();
        for(ConstraintPolygon polygon : polygons) {
            ArrayList<Segment<Vertex>> segments = polygon.segments;
            for(Segment<Vertex> segment : segments) {
                if(!constrainedVertices.contains(segment.start)) {
                    constrainedVertices.add(segment.start);
                }
                if(!constrainedVertices.contains(segment.end)) {
                    constrainedVertices.add(segment.end);
                }
            }
            for(int i = 0 ; i < segments.size() -1 ; i ++) {
                var segment = segments.get(i);
                var segmentNext = segments.get((i+1) % segments.size());
                HalfEdge edge = subdivision.getEdge(segment.start,segment.end);
                Face f = edge.incidentFace;
                var list = subdivision.faces.get(f);
                if(list == null) continue;
                Face finalF = f;
                boolean allToTheLeft = list.stream()
                        .allMatch(edge1 -> edge1.incidentFace != finalF || GeometryUtility.toTheLeftOf(edge.v,edge.twin.v,edge1.next.v) >= 0);
                // FIXME naive remove

                if(!allToTheLeft) {
                    f = edge.twin.incidentFace;
                    list = subdivision.faces.get(f);
                }

                for(HalfEdge edge1 : list) {
                    if(f == edge1.incidentFace) {
                        edge1.incidentFace = null;
                    }

                }

                subdivision.faces.remove(f);
            }
        }
    }

    private static void splitEdgeIfNecessary(SubdivisionBuilder subdivision, Segment<Vertex> segment, HalfEdge edgeIntersecting, Face f, Queue<Face> facesToCheck, ArrayList<HalfEdge> edgesToCheck) {
        Face newF = edgeIntersecting.twin.incidentFace;
        HalfEdge fisrt = edgeIntersecting.next;
        HalfEdge second = edgeIntersecting.twin.next;
        HalfEdge split1 = edgeIntersecting.next.next;
        HalfEdge split2 = edgeIntersecting.twin.next.next;
        var fMerged = subdivision.mergeFaces(f,newF, edgeIntersecting, edgeIntersecting.twin);
        if(newF == null) {
            return;
        }
        if(isQuadrilateralConvex(subdivision.faces.get(fMerged))) {
            var edges = subdivision.splitFace(fMerged,split1,split2);
            if(intersects(segment.start, segment.end,edges.left().v,edges.right().v)) {
                facesToCheck.add(fMerged);
            } else {
                edgesToCheck.add(edges.left());
            }
            //facesToCheck.add(edges.right().incidentFace);
        } else {
            subdivision.splitFace(fMerged,fisrt,second);
        }
    }

    public static boolean isQuadrilateralConvex(ArrayList<HalfEdge> edges) {
        if(edges.size() != 4) return false;
        for(int i = 0 ; i < edges.size() -1; i++) {
            Vertex v = edges.get(i).v;
            Vertex v2 = edges.get(i+1).v;
            Vertex toCheck = edges.get(i+1).twin.v;
            if(GeometryUtility.toTheLeftOf(v,v2,toCheck) < 0) {
                return false;
            }
        }
        return true;
    }

    public static class ConstraintPolygon {
        public ArrayList<Segment<Vertex>> segments;
        public ConstraintPolygon() {
            segments = new ArrayList<>();
        }
    }
}
