package utility.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.EdgeUtility;

import java.util.ArrayList;
import java.util.HashMap;

public class TestGraph2DBuilder {

    @Test
    public void testBuildGraph() {
        var builder = Graph2DTopologyBuilder.builder();

        builder.addEdge(new Vertex(0,0),new Vertex(1,0));
        builder.addEdge(new Vertex(1,0),new Vertex(1,1));
        builder.addEdge(new Vertex(1,1),new Vertex(0,1));
        builder.addEdge(new Vertex(0,1),new Vertex(0,0));

        Assertions.assertEquals(4,Graph2DTopologyBuilder.numberOfVertices(builder));
        Assertions.assertEquals(8,Graph2DTopologyBuilder.numberOfEdges(builder));
        Assertions.assertTrue(Graph2DTopologyBuilder.isCyclic(builder));
        builder.removeEdge(new Vertex(0,0),new Vertex(0,1));
        Assertions.assertEquals(4,Graph2DTopologyBuilder.numberOfVertices(builder));
        Assertions.assertEquals(6,Graph2DTopologyBuilder.numberOfEdges(builder));
        Assertions.assertFalse(Graph2DTopologyBuilder.isCyclic(builder));
        builder.addEdge(new Vertex(0,0),new Vertex(0,1));
        Assertions.assertTrue(Graph2DTopologyBuilder.isCyclic(builder));
        HashMap<Vertex,Vertex> parents = new HashMap<>();
        parents.put(new Vertex(0,0),new Vertex(0,0));
        var edges = builder.getCyclesListRec(new ArrayList<>(), new Vertex(0,0),new Vertex(0,0), parents);
        Assertions.assertEquals(1,edges.size());
        Assertions.assertEquals(new Edge(new Vertex(0,0),new Vertex(0,1)),edges.get(0));

        var cycle = builder.transformToCycle(edges.get(0),parents);
        ArrayList<Vertex> cyclePathExpected = new ArrayList<>();
        cyclePathExpected.add(new Vertex(0,0));
        cyclePathExpected.add(new Vertex(0,1));
        cyclePathExpected.add(new Vertex(1,1));
        cyclePathExpected.add(new Vertex(1,0));
        cyclePathExpected.add(new Vertex(0,0));
        Graph2DTopologyBuilder.Cycle cycleExpected = new Graph2DTopologyBuilder.Cycle();
        cycleExpected.cyclePath = cyclePathExpected;
        Assertions.assertEquals(cycleExpected, cycle);
    }

    @Test
    public void buildComplexGraph() {
        var builder = Graph2DTopologyBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0))
                .addEdge(new Vertex(1,0),new Vertex(1,1))
                .addEdge(new Vertex(0,0),new Vertex(1,1))
                .addEdge(new Vertex(1,0),new Vertex(2,1))
                .addEdge(new Vertex(1,1),new Vertex(2,1));
        Assertions.assertEquals(4,Graph2DTopologyBuilder.numberOfVertices(builder));
        Assertions.assertEquals(10,Graph2DTopologyBuilder.numberOfEdges(builder));
        Assertions.assertTrue(Graph2DTopologyBuilder.isCyclic(builder));
        HashMap<Vertex,Vertex> parents=  new HashMap<>();
        var listHeadCycles = builder.getCyclesListRec(new ArrayList<>(),new Vertex(0,0),new Vertex(0,0), parents);
        Assertions.assertEquals(2,listHeadCycles.size());
        Assertions.assertEquals(new Edge(new Vertex(0,0),new Vertex(1,1)),listHeadCycles.get(0));
        Assertions.assertEquals(new Edge(new Vertex(1,0),new Vertex(2,1)),listHeadCycles.get(1));
        var cycle1 = builder.transformToCycle(listHeadCycles.get(0),parents);
        Graph2DTopologyBuilder.Cycle cycle1Expected = new Graph2DTopologyBuilder.Cycle();
        cycle1Expected.addToEnd(new Vertex(0,0))
                .addToEnd(new Vertex(1,1))
                .addToEnd(new Vertex(1,0))
                .addToEnd(new Vertex(0,0));

        Assertions.assertEquals(cycle1Expected,cycle1);

        var cycle2 = builder.transformToCycle(listHeadCycles.get(1),parents);
        Graph2DTopologyBuilder.Cycle cycle2Expected = new Graph2DTopologyBuilder.Cycle();
        cycle2Expected.addToEnd(new Vertex(1,0))
                .addToEnd(new Vertex(2,1))
                .addToEnd(new Vertex(1,1))
                .addToEnd(new Vertex(1,0));

        Assertions.assertEquals(cycle2Expected,cycle2);
    }

    @Test
    public void getDistinctTrianglesTest() {
        var builder = Graph2DTopologyBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0))
                .addEdge(new Vertex(1,0),new Vertex(1,1))
                .addEdge(new Vertex(1,1),new Vertex(0,0))
                .addEdge(new Vertex(0,0),new Vertex(-1,1))
                .addEdge(new Vertex(-1,1),new Vertex(-1,0))
                .addEdge(new Vertex(-1,0),new Vertex(0,0))
                .addEdge(new Vertex(-1,1),new Vertex(1,1));

        var list = builder.getAllCyclesDistinct();
        Assertions.assertEquals(3,list.size());

    }
    @Test
    public void buildVeryComplexGraph() {
        var builder = Graph2DTopologyBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0))
                .addEdge(new Vertex(1,0),new Vertex(1,1))
                .addEdge(new Vertex(1,1),new Vertex(0,0))
                .addEdge(new Vertex(0,0),new Vertex(-1,0))
                .addEdge(new Vertex(-1,0),new Vertex(-1,1))
                .addEdge(new Vertex(-1,1),new Vertex(0,0))
                .addEdge(new Vertex(1,0),new Vertex(1,-1))
                .addEdge(new Vertex(1,-1),new Vertex(0,0))
                .addEdge(new Vertex(-1,0),new Vertex(-1,-1))
                .addEdge(new Vertex(-1,-1),new Vertex(0,0))
                .addEdge(new Vertex(1,0),new Vertex(5,1))
                .addEdge(new Vertex(5,1),new Vertex(5,10));

        Assertions.assertEquals(9,Graph2DTopologyBuilder.numberOfVertices(builder));
        Assertions.assertEquals(24,Graph2DTopologyBuilder.numberOfEdges(builder));
        Assertions.assertTrue(Graph2DTopologyBuilder.isCyclic(builder));
        HashMap<Vertex,Vertex> parents = new HashMap<>();
        parents.put(new Vertex(0,0),new Vertex(0,0));
        var listHeadCycles = builder.getCyclesListRec(new ArrayList<>(),new Vertex(0,0),new Vertex(0,0),parents);
        Assertions.assertEquals(4,listHeadCycles.size());

        Assertions.assertEquals(new Edge(new Vertex(0,0),new Vertex(1,1)),listHeadCycles.get(0));
        Assertions.assertEquals(new Edge(new Vertex(1,-1),new Vertex(0,0)),listHeadCycles.get(1));

        Assertions.assertEquals(new Edge(new Vertex(-1,1),new Vertex(0,0)),listHeadCycles.get(2));
        Assertions.assertEquals(new Edge(new Vertex(-1,-1),new Vertex(0,0)),listHeadCycles.get(3));

        var cycle1 = builder.transformToCycle(listHeadCycles.get(0),parents);
        var cycle1Expected = new Graph2DTopologyBuilder.Cycle();
        cycle1Expected.addToEnd(new Vertex(0,0))
                .addToEnd(new Vertex(1,1))
                .addToEnd(new Vertex(1,0))
                .addToEnd(new Vertex(0,0));

        Assertions.assertEquals(cycle1Expected,cycle1);

        var cycle2 = builder.transformToCycle(listHeadCycles.get(1),parents);
        var cycle2Expected = new Graph2DTopologyBuilder.Cycle();
        cycle2Expected.addToEnd(new Vertex(0,0))
                .addToEnd(new Vertex(1,-1))
                .addToEnd(new Vertex(1,0))
                .addToEnd(new Vertex(0,0));

        Assertions.assertEquals(cycle2Expected,cycle2);


    }
}
