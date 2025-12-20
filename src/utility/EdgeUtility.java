package utility;

import java.util.*;

public class EdgeUtility {

    /**
     * Connect two edges creating a cycle.
     * An face is not attach to the edges.
     * Attach e.twin.v with e2.v.
     * @param e
     * @param e2
     * @return The new edges created
     */
    public static Couple<HalfEdge,HalfEdge> connectEdges(HalfEdge e, HalfEdge e2) {

        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = e.twin.v;
        edgeTwin.v = e2.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;
        HalfEdge eNext = e2;
        HalfEdge ePrev = e;

        HalfEdge e2Next = e.twin;
        HalfEdge e2Prev = e2.twin;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = e2Next;
        edgeTwin.prev = e2Prev;

        e2Prev.next = edgeTwin;
        ePrev.next = edge;
        e2Next.prev = edgeTwin;
        eNext.prev = edge;

        return new Couple<>(edge,edgeTwin);
    }

    public static Couple<HalfEdge,HalfEdge> connectEdgesCW(HalfEdge e, HalfEdge e2) {

        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = e2.v;
        edgeTwin.v = e.twin.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;
        HalfEdge eNext = e.twin;
        HalfEdge ePrev = e2.twin;

        HalfEdge e2Next = e2;
        HalfEdge e2Prev = e;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = e2Next;
        edgeTwin.prev = e2Prev;

        e2Prev.next = edgeTwin;
        ePrev.next = edge;
        e2Next.prev = edgeTwin;
        eNext.prev = edge;

        return new Couple<>(edge,edgeTwin);
    }

    public static void inverseEdges(HalfEdge e) {

    }

    public static Couple<HalfEdge,HalfEdge> createEdgeFromVertices(MapPoint a, MapPoint b) {
        HalfEdge previous = new HalfEdge();
        HalfEdge twinPrevious = new HalfEdge();

        Vertex vPrevious = new utility.Vertex(a.getX(),a.getY());
        Vertex vPTwin = new utility.Vertex(b.getX(),b.getY());
        previous.v = vPrevious;
        twinPrevious.v = vPTwin;
        previous.twin = twinPrevious;
        twinPrevious.twin = previous;

        previous.next = twinPrevious;
        previous.prev = twinPrevious;
        twinPrevious.next = previous;
        twinPrevious.prev = previous;
        vPrevious.incidentEdge = previous;
        vPTwin.incidentEdge = twinPrevious;

        return new Couple<>(previous,twinPrevious);

    }

    /**
     * Transform the cycle the edge belongs to a Counter Clock wise cycle.
     * @param e
     */
    public static void transformCWCycleToCCW(HalfEdge e) {
        HalfEdge first = e;
        HalfEdge current = e;
        HalfEdge next = e;
        do {
            HalfEdge nextToSearch = next.prev;
            current = next;
            HalfEdge eNext = current.prev;
            HalfEdge ePrev = current.next;
            HalfEdge eTNext = current.prev.twin;
            HalfEdge eTPrev = current.next.twin;
            HalfEdge eTwin = current.twin;
            Vertex ve = current.twin.v;
            Vertex vT = current.v;
           /* var list = getAdjacentsEdges(current) ;
            for(HalfEdge edge : list) {
                if(edge != current && ePrev != edge) {
                    HalfEdge prev = edge.prev;
                    edge.prev = current.next.twin;
                    edge.twin.next = c
                }
            }*/
            current.v = ve;
            current.next = eNext;
            current.prev = ePrev;
            eTwin.prev = eTPrev;
            eTwin.next = eTNext;
            eTwin.v = vT;


            next = nextToSearch;
        }while (next != first);
    }

    /**
     * Determine if the cycle is counter clockwise
     * @param e
     * @return
     */
    public static boolean isCycleCCW(HalfEdge e) {
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge current = e;
        Queue<HalfEdge> file = new ArrayDeque<>();
        HashMap<HalfEdge, HalfEdge> parents = new HashMap<>();
        file.add(e);
        parents.put(e,e);
        double sum = 0;
        visited.add(e);
        visited.add(e.twin);
        HalfEdge head = current.prev;
        HalfEdge eNext = current;
        sum += (head.v.x * eNext.v.y) - (eNext.v.x * head.v.y);
        while (!file.isEmpty()) {
            head = file.poll();
             eNext = head.next;
            if(eNext == null) {
                System.err.println("Not a cycle.");
                return sum > 0;
            }
            if(!visited.contains(eNext)) {
                sum += (head.v.x * eNext.v.y) - (eNext.v.x * head.v.y);
                visited.add(eNext);
                visited.add(eNext.twin);
                file.add(eNext);
                parents.put(eNext,head);
            } else {
                if(!eNext.v.equals(head.v) && eNext != head.prev && eNext.v.equals(e.v)) {
                    return sum > 0;
                }
            }
        }
        System.err.println("Not a cycle.");
        return sum > 0;
    }

    public static boolean isCycleCCWUpTo(HalfEdge e,HalfEdge upTo) {
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge current = e;
        Queue<HalfEdge> file = new ArrayDeque<>();
        HashMap<HalfEdge, HalfEdge> parents = new HashMap<>();
        file.add(e);
        parents.put(e,e);
        double sum = 0;
        visited.add(e);
        visited.add(e.twin);
        HalfEdge head = current;
        HalfEdge eNext = current;
        while (!file.isEmpty()) {
            head = file.poll();
            eNext = head.prev;
            if(eNext == null) {
                System.err.println("Not a cycle.");
                return true;
            }
            sum += (eNext.v.x * head.v.y) - (head.v.x * eNext.v.y);
            if(!visited.contains(eNext) && eNext != upTo) {

                visited.add(eNext);
                visited.add(eNext.twin);
                file.add(eNext);
                parents.put(eNext,head);
            } else {
                if(!eNext.v.equals(head.v) && eNext != head.prev && eNext.v.equals(e.v)) {
                    return sum > 0;
                }
            }
        }
        System.err.println("Not a cycle.");
        return sum > 0;
    }

    public static Couple<HalfEdge,HalfEdge> connectEdgeToEs(HalfEdge e, HalfEdge eBase,HalfEdge eBPrev) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = eBase.v;
        edgeTwin.v = e.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        HalfEdge eNext = e;
        HalfEdge ePrev = eBPrev;

        HalfEdge eTNext = eBase;
        HalfEdge eTPrev = e.twin;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = eTNext;
        edgeTwin.prev = eTPrev;

        eTPrev.next = edgeTwin;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        eNext.prev = edge;

        return new Couple<>(edge,edgeTwin);
    }
    public static Couple<HalfEdge,HalfEdge> connectEdgeToEdgesCW(HalfEdge e, HalfEdge eBase,HalfEdge eBPrev) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = eBase.v;
        edgeTwin.v = e.twin.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        HalfEdge eNext = e.twin;
        HalfEdge ePrev = eBPrev.twin;

        HalfEdge eTNext = eBase;
        HalfEdge eTPrev = e;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = eTNext;
        edgeTwin.prev = eTPrev;

        eTPrev.next = edgeTwin;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        eNext.prev = edge;

        return new Couple<>(edge,edgeTwin);
    }
    public static Couple<HalfEdge,HalfEdge> connectEdgeToEdges2(HalfEdge e, HalfEdge eRight,HalfEdge eLeft) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = e.twin.v;
        edgeTwin.v = eRight.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;
        HalfEdge eNext = eRight;
        HalfEdge ePrev = e;

        HalfEdge eTNext = e.twin;
        HalfEdge eTPrev = eLeft;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.prev = eTPrev;
        edgeTwin.next = eTNext;

        eRight.prev = edge;
        eLeft.next = edgeTwin;
        eTPrev.next = edgeTwin;
        eNext.prev = edge;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        return new Couple<>(edge,edgeTwin);


    }

    /**
     * Return a new edges from 4 edges
     * @param eLeft The left edge
     * @param eRight the right edge
     * @param e2Left the left edge of the connection end
     * @param e2Right
     * @return
     */
    public static Couple<HalfEdge,HalfEdge> connectEdgesToEdges(HalfEdge eLeft,HalfEdge eRight, HalfEdge e2Left, HalfEdge e2Right) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = eRight.v;
        edgeTwin.v = e2Right.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        HalfEdge eNext = e2Right;
        HalfEdge ePrev = eLeft;
        HalfEdge eTNext = eRight;
        HalfEdge eTPrev = e2Left;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = eTNext;
        edgeTwin.prev = eTPrev;

        eNext.prev = edge;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        eTPrev.next = edgeTwin;

        return new Couple<>(edge,edgeTwin);
    }

    public static Couple<HalfEdge,HalfEdge> connectEdgesToEdgesCW(HalfEdge eLeft,HalfEdge eRight, HalfEdge e2Left, HalfEdge e2Right) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = e2Right.v;
        edgeTwin.v = eRight.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        HalfEdge eNext = eRight;
        HalfEdge ePrev = e2Left;
        HalfEdge eTNext = e2Right;
        HalfEdge eTPrev = eLeft;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = eTNext;
        edgeTwin.prev = eTPrev;

        eNext.prev = edge;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        eTPrev.next = edgeTwin;

        return new Couple<>(edge,edgeTwin);
    }

    public static Couple<HalfEdge,HalfEdge> connectEdgeToEdges(HalfEdge e, HalfEdge eBase,HalfEdge eBPrev) {
        HalfEdge edge = new HalfEdge();
        HalfEdge edgeTwin = new HalfEdge();
        edge.v = e.twin.v;
        edgeTwin.v = eBase.v;
        edge.twin = edgeTwin;
        edgeTwin.twin = edge;

        if(GeometryUtility.toTheLeftOf(eBPrev.v,eBPrev.twin.v,e.v) > 0) {

        }
        HalfEdge eNext = eBPrev.twin;
        HalfEdge ePrev = e;

        HalfEdge eTNext = e.twin;
        HalfEdge eTPrev = eBase.twin;

        edge.next = eNext;
        edge.prev = ePrev;
        edgeTwin.next = eTNext;
        edgeTwin.prev = eTPrev;

        eTPrev.next = edgeTwin;
        ePrev.next = edge;
        eTNext.prev = edgeTwin;
        eNext.prev = edge;

        return new Couple<>(edge,edgeTwin);
    }

    /**
     * Connect the edge to the
     * @param e
     * @param v2
     */
    public static Couple<HalfEdge,HalfEdge> connectEdgeToVertex(HalfEdge e, Vertex v2) {
        boolean isCycle = isInCycle(e);

        // point d’attache : twin si cycle, sinon e lui-même
        HalfEdge anchor = isCycle ? e.twin : e;

        HalfEdge e2 = new HalfEdge();
        HalfEdge e2Twin = new HalfEdge();

        // Origines
        e2.v = anchor.v;
        e2Twin.v = v2;

        // Twins
        e2.twin = e2Twin;
        e2Twin.twin = e2;

        e2Twin.incidentFace = null;

        // Si l'arête fait partie d'un cycle → insertion dans la boucle
        if (isCycle) {
            HalfEdge p = anchor.prev;
            p.next = e2;
            e2.prev = p;
            e2.next = anchor;
            anchor.prev = e2;
        }
        // Sinon → demi-arête isolée
        else {
            e2.next = e2Twin;
            e2.prev = anchor;
            anchor.next = e2;
            e2Twin.next = anchor.twin;
            e2Twin.prev = e2;
            anchor.twin.prev = e2Twin;
        }

        return new Couple<>(e2,e2Twin);
    }

    /**
     * Connect an edge to a vertex
     * @param e The edge to attach
     * @param v the vertex to connect
     * @return The new edges created (e(v) -> new edges(v2))
     */
    public static Couple<HalfEdge,HalfEdge> connectVertex(HalfEdge e, Vertex v) {
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        e1.v = e.twin.v;
        v.incidentEdge = e1;
        eTwin.v = v;
        eTwin.twin = e1;
        e1.twin = eTwin;
        connectEdges(e,e1,eTwin);
        return new Couple<>(e1,eTwin);

    }
    public static Couple<HalfEdge,HalfEdge> connectVertexCW(HalfEdge e, Vertex v) {
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        e1.v = v;
        v.incidentEdge = e1;
        eTwin.v = e.twin.v;
        eTwin.twin = e1;
        e1.twin = eTwin;
        HalfEdge eTwinNext= e1;
        HalfEdge eTwinPrev = e;
        HalfEdge eNext = e.twin;
        HalfEdge ePrev = eTwin;

        e1.prev = ePrev;
        e1.next = eNext;
        eTwin.next = eTwinNext;
        eTwin.prev = eTwinPrev;

        e.next = eTwin;
        e.twin.prev = e1;
        return new Couple<>(e1,eTwin);

    }

    public static Couple<HalfEdge,HalfEdge> connectVertexToBothEdge2(HalfEdge e, HalfEdge e2, Vertex v) {
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        e1.v = e2.v;

        eTwin.v = v;
        eTwin.twin = e1;
        e1.twin = eTwin;
        v.incidentEdge = e1;

        HalfEdge eNext = eTwin;
        HalfEdge ePrev = e;
        HalfEdge eTNext = e2;
        HalfEdge eTPrev = e1;

        e1.next = eNext;
        e1.prev = ePrev;
        eTwin.next = eTNext;
        eTwin.prev = eTPrev;

        e.next = e1;
        e2.prev = eTwin;
        return new Couple<>(e1,eTwin);
    }
    /**
     * Connect a Vertex to two edges connected.
     * The attachment depends on the position in space of the vertex compared to the two edges.
     * The edges are not attached to a face.
     * Create an adjacent edge to e2.origin
     * @param e The first edge to attach to
     * @param e2 The second edge to attach to
     * @param v The vertex to connect
     * @return The new edges created
     */
    public static Couple<HalfEdge,HalfEdge> connectVertexToBothEdge(HalfEdge e, HalfEdge e2, Vertex v) {
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        e1.v = e2.v;

        eTwin.v = v;
        eTwin.twin = e1;
        e1.twin = eTwin;
        v.incidentEdge = e1;

        if(GeometryUtility.toTheLeftOf(e.v,e2.v,v) > 0 && GeometryUtility.toTheLeftOf(e2.v,e2.twin.v,v) > 0) {
            // Connect with the normalEdges
            HalfEdge eNext = eTwin;
            HalfEdge ePrev = e;
            HalfEdge eTNext = e2;
            HalfEdge eTPrev = e1;

            e1.next = eNext;
            e1.prev = ePrev;
            eTwin.next = eTNext;
            eTwin.prev = eTPrev;

            e.next = e1;
            e2.prev = eTwin;
        } else if ( GeometryUtility.toTheLeftOf(e2.v,e2.twin.v,v) > 0) {
            // Second normal edge
            HalfEdge eNext = eTwin;
            HalfEdge ePrev = e2.twin;
            HalfEdge eTNext = e;
            HalfEdge eTPrev = e1;

            e1.next = eNext;
            e1.prev = ePrev;
            eTwin.next = eTNext;
            eTwin.prev = eTPrev;

            e.prev = eTwin;
            e2.twin.next = e1;
        } else {
            // Connect with the twins
            HalfEdge eNext = eTwin;
            HalfEdge ePrev = e2.twin;
            HalfEdge eTNext = e.twin;
            HalfEdge eTPrev = e1;
            e1.next = eNext;
            e1.prev = ePrev;
            eTwin.next = eTNext;
            eTwin.prev = eTPrev;

            e.twin.prev = eTwin;
            e2.twin.next = e1;
        }
        return new Couple<>(e1,eTwin);
    }

    public static Couple<HalfEdge,HalfEdge> connectVertexToBothEdgeCW(HalfEdge e, HalfEdge e2, Vertex v) {
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        e1.v = v;

        eTwin.v = e2.v;
        eTwin.twin = e1;
        e1.twin = eTwin;
        v.incidentEdge = e1;


            // Connect with the twins
            HalfEdge eNext = e2;
            HalfEdge ePrev = eTwin;
            HalfEdge eTNext = e1;
            HalfEdge eTPrev = e;
            e1.next = eNext;
            e1.prev = ePrev;
            eTwin.next = eTNext;
            eTwin.prev = eTPrev;

            e.next = eTwin;
            e2.prev = e1;

        return new Couple<>(e1,eTwin);
    }



    public static HalfEdge getAntiHoraireMost(Vertex v) {
        HalfEdge first = v.incidentEdge;
        HalfEdge current = first;
        HalfEdge nextV= first.twin.next;
        while (first != nextV && nextV.v.equals(v)) {
            current = nextV;
            nextV = current.twin.next;
        }
        return current;
    }
    /**
     * Connect edges to e. e is not in a cycle
     * @param e
     * @param hfConnect
     * @param hfConnectTwin
     */
    public static void connectEdges(HalfEdge e, HalfEdge hfConnect, HalfEdge hfConnectTwin) {
        hfConnect.twin = hfConnectTwin;
        hfConnectTwin.twin = hfConnect;
        HalfEdge eTwinNext= e.twin;
        HalfEdge eTwinPrev = hfConnect;
        HalfEdge eNext = hfConnectTwin;
        HalfEdge ePrev = e;

        hfConnect.prev = ePrev;
        hfConnect.next = eNext;
        hfConnectTwin.next = eTwinNext;
        hfConnectTwin.prev = eTwinPrev;

        e.next = hfConnect;
        e.twin.prev = hfConnectTwin;
    }

    public static void connectEdgeWithFirstInCycle(HalfEdge e, HalfEdge e2, HalfEdge e2Twin) {
        HalfEdge anchor = e.twin;
        HalfEdge eTwinNext= anchor.next;
        HalfEdge eTwinPrev = e2;
        HalfEdge eNext = e2Twin;
        HalfEdge ePrev = anchor;
        e2.prev = ePrev;
        anchor.next = e2;
        e2.next = eNext;
        e2Twin.next = eTwinNext;
        e2Twin.prev = eTwinPrev;
        eTwinNext.prev = e2Twin;
    }

    /**
     * Récupére l'arc le plus dans le sens horaire à partir du vertex.
     * @param v
     * @return
     */
    public static HalfEdge getHoraryMostEdgeFrom(Vertex v) {
        HalfEdge first = v.incidentEdge;
        HalfEdge current = first;
        HalfEdge nextV= first.twin.prev;
        while (first != nextV && nextV.v.equals(v)) {
            current = nextV;
            nextV = current.twin.prev;
        }
        return current;

    }
    public static ArrayList<HalfEdge> getAdjacentsEdgesSameDir(HalfEdge edge) {
        ArrayList<HalfEdge> edges = new ArrayList<>();
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge first =edge;

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
    public static ArrayList<HalfEdge> getAdjacentsEdges(HalfEdge edge) {
        ArrayList<HalfEdge> edges = new ArrayList<>();
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge first =edge;

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

    /**
     * Get the left and right of edge that are closest to toCompare (right and left)
     * The edge compared are adjacents to edge.origin.
     * @param edge
     * @param toCompare
     * @return
     */
    public static Couple<HalfEdge,HalfEdge> getLeftAndRightOf(HalfEdge edge, HalfEdge toCompare) {
        var list = getAdjacentsEdges(edge);
        return getLeftAndRightOf(toCompare,list);

    }
    public static Couple<HalfEdge,HalfEdge> getLeftAndRightOfNoTwin(HalfEdge edge, HalfEdge toCompare) {
        var list = getAdjacentsEdgesSameDir(edge);
        return getLeftAndRightOf(toCompare,list);

    }
    public static Couple<HalfEdge,HalfEdge> getLeftAndRightOf( HalfEdge toCompare,List<HalfEdge> list) {
        ArrayList<Double> angles = new ArrayList<>();
        PriorityQueue<Couple<Double,HalfEdge>> queue = new PriorityQueue<>((o1,o2) -> -Double.compare(o1.left(),o2.left()));
        double[] toCompareNormalized = toCompare.normalized();
        for(HalfEdge edge1 : list) {
            if(edge1 == toCompare)continue;
            double[] normalized = edge1.normalized();
            double cross = GeometryUtility.toTheLeftOf(new Point(0,0),new Point(toCompareNormalized[0],toCompareNormalized[1])
                    ,new Point(normalized[0],normalized[1]));
            double dot = GeometryUtility.dotProduct(new Point(0,0),new Point(toCompareNormalized[0],toCompareNormalized[1])
                    ,new Point(normalized[0],normalized[1]));
            //double dotProduct = GeometryUtility.dotProduct(toCompare.v,toCompare.twin.v,edge1.twin.v);
            //dotProduct = dotProduct > 0 ? dotProduct >= 1 ? dotProduct+1 *dotProduct+1 : 2 : 1;
            queue.offer(new Couple<Double,HalfEdge>((Math.signum(cross) * (1+dot)),edge1));
            //queue.offer(new Couple<>(GeometryUtility.toTheLeftOf(toCompare.v,toCompare.twin.v,edge1.twin.v) * dotProduct,edge1));

        }
        HalfEdge left = queue.poll().right();
        int iterationsCount = queue.size() -1;
        for (int i = 0 ; i < iterationsCount; i++) {
            queue.poll();
        }
        HalfEdge right = null;
        if(queue.isEmpty()) {
            right = left.twin;
        } else {
             right = queue.peek().right();
        }


        return new Couple<>(left,right);

    }

    public static void connectWithBothInCycles(HalfEdge e, HalfEdge e2, HalfEdge hfConnect, HalfEdge hfConnectTwin) {
        HalfEdge eTwinNext= e.twin;
        HalfEdge eTwinPrev = e2.twin.prev;
        HalfEdge eNext = e2.twin;
        HalfEdge ePrev = e.twin.prev;

        hfConnect.next = eNext;
        hfConnectTwin.next = eTwinNext;
        hfConnect.prev = ePrev;
        hfConnectTwin.prev = eTwinPrev;

        eNext.prev = hfConnect;
        ePrev.next = hfConnect;
        eTwinPrev.next = hfConnectTwin;
        eTwinNext.prev = hfConnectTwin;
    }

    /**
     * Is the edge in a cycle. Meaning it creates a face.
     * @param e
     * @return
     */
    public static boolean isInCycle(HalfEdge e) {
        ArrayList<HalfEdge> visited = new ArrayList<>();
        HalfEdge current = e;
        Queue<HalfEdge> file = new ArrayDeque<>();
        HashMap<HalfEdge, HalfEdge> parents = new HashMap<>();
        file.add(e);
        parents.put(e,e);
        visited.add(e);
        visited.add(e.twin);
        while (!file.isEmpty()) {
            HalfEdge head = file.poll();
            HalfEdge eNext = head.next;
            if(eNext == null) {
                return false;
            }
            if(!visited.contains(eNext)) {
                visited.add(eNext);
                visited.add(eNext.twin);
                file.add(eNext);
                parents.put(eNext,head);
            } else {
                if(!eNext.v.equals(head.v) && eNext != head.prev && eNext.v.equals(e.v)) {
                    return true;
                }
            }
        }
        return false;

    }


}
