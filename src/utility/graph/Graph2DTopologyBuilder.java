package utility.graph;


import utility.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * An 2D non oriented graph builder
 */
public class Graph2DTopologyBuilder {
    HashMap<Vertex, ArrayList<Vertex>> verticesMap ;

    Graph2DTopologyBuilder() {
        verticesMap = new HashMap<>();
    }

    public void removeVertex(Vertex v) {
        var listV = verticesMap.get(v);



            for (Vertex vertex : listV) {
                verticesMap.get(vertex).remove(v);
            }

        verticesMap.remove(v);
    }
    public Vertex getFirstVertex() {
        return verticesMap.keySet().stream().toList().getFirst();
    }
    public static Graph2DTopologyBuilder builder() {
        return new Graph2DTopologyBuilder();
    }

    public boolean exists( Vertex v1) {
        return verticesMap.containsKey(v1);
    }

    public boolean hasEdge(Vertex v1, Vertex v2) {

        return verticesMap.get(v1).contains(v2);
    }

    public static ArrayList<Vertex> getEdges(Graph2DTopologyBuilder builder, Vertex v1) {
        return builder.verticesMap.getOrDefault(v1,new ArrayList<>());
    }

    public Edge getEdge(Vertex v1, Vertex v2) {
        return new Edge(v1,v2);
    }

    public Graph2DTopologyBuilder addEdge(Vertex v1, Vertex v2) {
        var voisinsV1 = verticesMap.getOrDefault(v1,new ArrayList<>());
        var voisinsV2 = verticesMap.getOrDefault(v2,new ArrayList<>());
        voisinsV1.add(v2);
        voisinsV2.add(v1);
        verticesMap.putIfAbsent(v1,voisinsV1);
        verticesMap.putIfAbsent(v2,voisinsV2);
        return this;
    }

    public Graph2DTopologyBuilder removeEdge(Vertex v1, Vertex v2) {
        var voisinsV1 = verticesMap.get(v1);
        var voisinsV2 = verticesMap.get(v2);
        voisinsV1.remove(v2);
        voisinsV2.remove(v1);
        return this;
    }

    private SubRepresentation buildSubRepresentationFrom(Vertex v) {

       var cycles = getCyclesListRec(v);
       return new SubRepresentation(cycles);
    }

    private class SubRepresentation {
        List<Cycle> cycles;

        SubRepresentation(List<Cycle> cycles) {
            this.cycles = cycles;
        }
    }

    private void transformSubRepresentationToSubdivision(SubRepresentation subRepresentation) {
        Subdivision subdivision = new Subdivision();
        for(Cycle c : subRepresentation.cycles) {
            Face fCycle = new Face();
            HalfEdge previousHf  = null;
            for(int i = 0 ; i < c.cyclePath.size() -1; i ++) {
                Vertex v = c.cyclePath.get(i);
                Vertex v2 = c.cyclePath.get(i);

            }
        }
    }


    public List<Cycle> getCyclesListRec(Vertex v) {
        HashMap<Vertex,Vertex> parents = new HashMap<>();
        ArrayList<Vertex> visited = new ArrayList<>();
        parents.put(v,v);
        var listHead = getCyclesListRec(visited,v,v,parents);
        var cycles = listHead.stream().map(e -> transformToCycle(e,parents)).toList();
        return cycles;
    }
    public List<Cycle> getCyclesListLarge(Vertex v) {
        HashMap<Vertex,Vertex> parents = new HashMap<>();
        var listHead = getCyclesListLarge(v,parents);
        var cycles = listHead.stream().map(e -> transformToCycle(e,parents)).toList();
        return cycles;
    }

    public void showGraph() {
        JFrame frame = new JFrame("Triangulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph((Graphics2D) g);
            }
        };

        frame.add(panel);
        frame.setVisible(true);

    }
    private void drawGraph(Graphics2D g2) {
        for(Vertex v : verticesMap.keySet()) {
            Ellipse2D ellipse2D = new Ellipse2D.Double(v.x*20,v.y*20,5,5);
            g2.draw(ellipse2D);
            for(Vertex voisin : verticesMap.get(v)) {

                Line2D lin = new Line2D.Double(v.x*20, v.y*20, voisin.x*20, voisin.y*20);
                //g2.drawLine((int) v.x, (int) v.y, (int) voisin.x, (int) voisin.y);
                g2.draw(lin);
            }
        }
    }

    public Cycle transformToCycle(Edge edgeTerminating, HashMap<Vertex, Vertex> parents) {
        Cycle cycle = new Cycle();
        Vertex current = parents.get(edgeTerminating.v1());
        Vertex head = (Vertex) edgeTerminating.v2();

        cycle.addToEnd(head);
        cycle.addToEnd((Vertex) edgeTerminating.v1());
        cycle.addToEnd(current);
        Vertex next = parents.get(current);
        while (!next.equals( head) && !parents.get(next).equals(next)){
            cycle.addToEnd(next);
            next = parents.get(next);
        }
        cycle.addToEnd(head);
        return cycle;

    }

    public ArrayList<Edge> getCyclesListLarge(Vertex v, HashMap<Vertex, Vertex> parents) {
        Set<Vertex> visited = new HashSet<>();
        Queue<Vertex> file = new ArrayDeque<>();
        visited.add(v);
        file.add(v);
            ArrayList<Edge> cyclesHead = new ArrayList<>();
        parents.put(v,v);
        while (!file.isEmpty()) {
            var head = file.poll();
            for(Vertex voisin : getEdges(this,head)) {
                if(!visited.contains(voisin)) {
                    file.add(voisin);
                    visited.add(voisin);
                    parents.put(voisin,head);
                } else if((!parents.get(head).equals(voisin))) {
                    Edge edge = new Edge(head,voisin);
                    if(!cyclesHead.stream().anyMatch(e ->e.nonOrientedEquals(edge) )) {
                        cyclesHead.add(edge);
                    }
                }
            }
        }
        return cyclesHead;
    }
    /**
     * Advent of code mateo
     * ////////////////////////////////////////////
     * /////////////////////////////////////////
     * @param visited
     * @param v
     * @param pere
     * @param parents The parents list of the vertices
     * @return the list of the edges creating a cycle
     */
    public ArrayList<Edge> getCyclesListRec(ArrayList<Vertex> visited, Vertex v, Vertex pere, HashMap<Vertex, Vertex> parents) {
        ArrayList<Edge> cyclesHead = new ArrayList<>();
        visited.add(v);
        for (Vertex voisin : getEdges(this,v)) {
            if(!visited.contains(voisin)) {
                parents.put(voisin,v);
                cyclesHead.addAll(getCyclesListRec(visited,voisin,v,parents));
            } else if (!pere.equals(voisin)) {
                Edge edge = new Edge(v,voisin);
                if(!cyclesHead.stream().anyMatch(e ->e.nonOrientedEquals(edge) )) {
                    cyclesHead.add(edge);
                }

            }
        }
        return cyclesHead;
    }

    public ArrayList<Vertex> getLeaves(HashMap<Vertex,Vertex> parents) {
        ArrayList<Vertex> remaining = new ArrayList<>(verticesMap.keySet());

        for(Vertex v : parents.keySet()) {
            Vertex value = parents.get(v);
            if(remaining.contains(value)) {
                remaining.remove(value);
            }
        }
        return remaining;
    }

    public static class Cycle {
        public ArrayList<Vertex> cyclePath;
        public Cycle() {
            cyclePath = new ArrayList<>();
        }

        public Cycle addToEnd(Vertex v) {
            cyclePath.add(v);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Cycle cycle)
                    ||cycle.cyclePath.size() != this.cyclePath.size()) return false;
            for(int i = 0 ; i < this.cyclePath.size(); i ++) {
                if(!cyclePath.get(i).equals(cycle.cyclePath.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(cyclePath);
        }

        @Override
        public String toString() {
            return "Cycle : " + cyclePath.stream().map(vertex -> vertex + " ->" );
        }
    }

    public int numberOfEdges(Vertex v) {
        return verticesMap.get(v).size();
    }

    public Subdivision build() {
        Subdivision building = new Subdivision();
        Vertex v0 = verticesMap.keySet().stream().toList().get(0);
        var sub = buildSubRepresentationFrom(v0);
        HashMap<Vertex,ArrayList<HalfEdge>> inserted = new HashMap<>() {
        };
        Set<Vertex> verticesInserted = new HashSet<>();
        for(Cycle cycle: sub.cycles) {
            Face f = new Face();
            Cycle c = cycle;
            // IF the cycle is CW tranform it to CCW
            if(!isCycleCCW(c)) {
                c = transformCycleToCCW(c);
            }
            Couple<HalfEdge,HalfEdge> first = null;
            if(!verticesInserted.contains(c.cyclePath.get(0))) {
                first = EdgeUtility.createEdgeFromVertices(c.cyclePath.get(0),c.cyclePath.get(1));
            } else {
                Vertex v = c.cyclePath.get(0);
                Vertex v2 = c.cyclePath.get(1);
                HalfEdge edge = new HalfEdge();
                HalfEdge twin = new HalfEdge();
                edge.v = new utility.Vertex(v.x,v.y);
                twin.v = new utility.Vertex(v2.x,v2.y);
                edge.twin = twin;
                twin.twin = edge;
                var leftAndRight = EdgeUtility.getLeftAndRightOf(inserted.get(v).get(0),edge);

                first = EdgeUtility.connectVertexToBothEdge(leftAndRight.right(),leftAndRight.left()
                        ,new utility.Vertex(v2.x,v2.y));
            }
            var previous = first.left();
            first.left().incidentFace = f;
       cycles:     for(int i = 1 ; i < c.cyclePath.size() -1 ; i++) {
                HalfEdge vBefore = previous;
                Vertex vi = c.cyclePath.get(i);
                if(verticesInserted.contains(vi)) {
                    Vertex v = c.cyclePath.get(i-1);
                    Vertex v2 = c.cyclePath.get(i);
                    HalfEdge edge = new HalfEdge();
                    HalfEdge twin = new HalfEdge();
                    edge.v = new utility.Vertex(v.x,v.y);
                    twin.v = new utility.Vertex(v2.x,v2.y);
                    edge.twin = twin;
                    twin.twin = edge;
                    var leftAndRight = EdgeUtility.getLeftAndRightOf(inserted.get(v).get(0),edge);

                    EdgeUtility.connectEdgeToEdges(vBefore,leftAndRight.left(),leftAndRight.right());
                    continue cycles;
                } else {
                    var newH = EdgeUtility.connectVertex(vBefore,new utility.Vertex(vi.x,vi.y));
                    previous = newH.left();
                    newH.left().incidentFace = f;
                }
                //Vertex vi2 = c.cyclePath.get((i+1) % c.cyclePath.size());

            }
            if(EdgeUtility.getAdjacentsEdges(first.left()).size() > 1) {

            }
            var endEdge = EdgeUtility.connectEdges(previous,first.left());

        }
        return building;
    }

    public static boolean isCycleCCW(Cycle c) {
        ArrayList<Vertex> visited = new ArrayList<>();
        Vertex current = c.cyclePath.get(1);
        Queue<Vertex> file = new ArrayDeque<>();
        file.add(current);
        double sum = 0;
        visited.add(current);

        int i = 1;
        Vertex head = c.cyclePath.get(0);
        Vertex eNext = current;
        sum += (head.x * eNext.y) - (eNext.x * head.y);
        i++;
        while (!file.isEmpty() && i != c.cyclePath.size()) {
            head = file.poll();
            eNext = c.cyclePath.get(i);
            if(eNext == null) {
                System.err.println("Not a cycle.");
                return sum > 0;
            }
            if(!visited.contains(eNext)) {
                sum += (head.x * eNext.y) - (eNext.x * head.y);
                visited.add(eNext);
                file.add(eNext);
            }
            i++;
        }
        //System.err.println("Not a cycle.");
        return sum > 0;
    }

    public static Cycle transformCycleToCCW(Cycle c) {
        Cycle newC = new Cycle();
        List<Vertex> reversed = c.cyclePath.reversed();
        for(Vertex v : reversed) {
            newC.addToEnd(v);
        }

        return newC;
    }

    public Graph2DTopologyBuilder removeUnusedVertices() {
        verticesMap.forEach((vertex,edges) -> {
            if(edges.size() == 0) {
                verticesMap.remove(vertex);
            }
        });
        return this;
    }

    public static ArrayList<Vertex> adjacentTo(Graph2DTopologyBuilder builder,Vertex v1) {
        return builder.verticesMap.getOrDefault(v1,new ArrayList<>());
    }

    public static int numberOfVertices(Graph2DTopologyBuilder builder) {
        return builder.verticesMap.size();
    }

    public static int numberOfEdges(Graph2DTopologyBuilder builder) {
        return builder.verticesMap.values().stream().mapToInt(ArrayList::size).sum();
    }

    public static boolean isCyclic(Graph2DTopologyBuilder builder) {
        Vertex v= builder.verticesMap.keySet().stream().toList().get(0);
        return cyclicRec(builder,v,new ArrayList<>(),v);
    }

    public static boolean cyclicRec(Graph2DTopologyBuilder builder,Vertex v,ArrayList<Vertex> visited,Vertex pere) {
        visited.add(v);
        for (Vertex voisin : getEdges(builder,v)) {
            if(!visited.contains(voisin)) {
                if (cyclicRec(builder,voisin,visited,v)) return true;
            } else {
                if( !voisin.equals( pere)) {
                    return true;
                }
            }
        }
        return false;
    }


}
