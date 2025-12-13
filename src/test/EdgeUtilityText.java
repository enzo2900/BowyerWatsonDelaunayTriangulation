package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.EdgeUtility;
import utility.HalfEdge;
import utility.Vertex;

import java.util.ArrayList;

public class EdgeUtilityText {

    @Test
    public void testIsInCycle() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,1));

        Assertions.assertFalse(EdgeUtility.isInCycle(builder.D.getEdge(new Vertex(0,0),new Vertex(1,1))));
        builder.Vertex(new Vertex(0, 1)).Vertex(new Vertex(0,0));
        Assertions.assertTrue(EdgeUtility.isInCycle(builder.D.getEdge(new Vertex(0,0),new Vertex(1,1))));

    }

    @Test
    public void testConnectEdge() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0));
        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(1,0));
        var newV = EdgeUtility.connectVertex(first,new Vertex(1,1));
       EdgeUtility.connectEdges(newV.left(),first);
        Assertions.assertTrue(EdgeUtility.isInCycle(first));
    }

    @Test
    public void testConnectWithTwoEdges() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0));
        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(1,0));
        var newE = EdgeUtility.connectVertex(first,new Vertex(1,1));
        Assertions.assertEquals(first.twin.prev.v,newE.right().v);
        var newE2 = EdgeUtility.connectVertexToBothEdge(first,newE.left(),new Vertex(2,0));
        Assertions.assertEquals(first.twin.prev.v,newE2.right().v);
        var newE3 = EdgeUtility.connectVertexToBothEdge(first,newE.left(),new Vertex(0,1));
        Assertions.assertEquals(first.next.twin.v,newE3.right().v);
    }

    @Test
    public void connectingTwoCycles() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0));
        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(1,0));
        var newE1 = EdgeUtility.connectVertex(first,new Vertex(1,1));
        var connect1 = EdgeUtility.connectEdges(newE1.left(),first);
        Assertions.assertTrue(EdgeUtility.isInCycle(first));

        var newE2 = EdgeUtility.connectVertexToBothEdge(first,newE1.left(),new Vertex(2,0));
        EdgeUtility.connectEdgeToEdges(newE2.left(),connect1.left(),newE1.left());
        Assertions.assertTrue(EdgeUtility.isInCycle(newE1.right()));
        Assertions.assertTrue(EdgeUtility.isCycleCCW(first));
    }

    @Test
    public void cycleCWToCCW() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0)).Vertex(new Vertex(-1,0))
                .Vertex(new Vertex(-1,1))
                .Vertex(new Vertex(0,0));

        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(-1,0));
        Assertions.assertTrue(!EdgeUtility.isCycleCCW(first));
        EdgeUtility.transformCWCycleToCCW(first);
        Assertions.assertTrue(EdgeUtility.isCycleCCW(first));
    }

    @Test
    public void testNumberOfAdjacents() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0));
        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(1,0));
        var newE = EdgeUtility.connectVertex(first,new Vertex(1,1));
        EdgeUtility.connectVertexToBothEdge(first,newE.left(),new Vertex(1,-1));
        Assertions.assertEquals(3,EdgeUtility.getAdjacentsEdges(newE.left()).size());
    }

    @Test
    public void testGetAdjacentTo() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(0,0),new Vertex(1,0));
        HalfEdge first = builder.D.getEdge(new Vertex(0,0),new Vertex(1,0));
        var newE = EdgeUtility.connectVertex(first,new Vertex(1,1));
        var newE2 = EdgeUtility.connectVertexToBothEdge(first,newE.left(),new Vertex(1,-1));
        var newE3 = EdgeUtility.connectVertexToBothEdge(newE2.left(),newE.left(),new Vertex(2,0));
        ArrayList<HalfEdge> edges = new ArrayList<>();

        var adjacents = EdgeUtility.getLeftAndRightOf(newE.left(),newE3.left());
        Assertions.assertSame(adjacents.left(),newE.left());
        Assertions.assertSame(adjacents.right(),newE2.left());

    }


    @Test
    public void connectingTwoCyclesReverse() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .addEdge(new Vertex(-1,0),new Vertex(0,0));
        HalfEdge first = builder.D.getEdge(new Vertex(-1,0),new Vertex(0,0));
        var newE = EdgeUtility.connectVertex(first,new Vertex(-1,1));
        var connect1 = EdgeUtility.connectEdges(newE.left(),first);
        Assertions.assertTrue(EdgeUtility.isCycleCCW(connect1.left()));
        HalfEdge edge = new HalfEdge();
        edge.v = first.v;
        HalfEdge twin = new HalfEdge();
        edge.twin = twin;
        twin.v = new Vertex(-2,1);
        var c = EdgeUtility.getLeftAndRightOf(first,edge);
        var newE2 = EdgeUtility.connectVertexToBothEdge(c.right(),c.left(),new Vertex(-2,1));
        var newE3 = EdgeUtility.connectVertex(newE2.left(),new Vertex(-1,3));
        boolean isCycleCCW = EdgeUtility.isCycleCCWUpTo(newE3.left(),newE2.left());
        EdgeUtility.connectEdgeToEdges(newE3.left(),connect1.left(),newE.left());
        //var newE4 = EdgeUtility.connectEdgeToEdgesCW(newE3.left(),connect1.left(),newE.left());

        // EdgeUtility.connectEdgeToEdges(newE3.left(),connect1.left(),newE.left());
        Assertions.assertTrue(EdgeUtility.isInCycle(newE2.right()));
        Assertions.assertTrue(EdgeUtility.isCycleCCW(newE3.right()));

        //EdgeUtility.transformCWCycleToCCW(newE2.right());
        //Assertions.assertTrue(EdgeUtility.isCycleCCW(newE2.right()));
    }

    @Test
    public void transformToCCWDelicate() {
       var builder = HalfEdge.SubdivisionBuilder.builder()
               .Vertex(new Vertex(1,0))
               .Vertex(new Vertex(0,0))
               .Vertex(new Vertex(0,1))
               .Vertex(new Vertex(1,1))
               .Vertex(new Vertex(1,0));

       var fisrt = builder.D.getEdge(new Vertex(1,0),new Vertex(0,0));
        var second = builder.D.getEdge(new Vertex(0,0),new Vertex(0,1));
        EdgeUtility.transformCWCycleToCCW(fisrt);
        var newE = EdgeUtility.connectVertexToBothEdge(fisrt,second,new Vertex(-1,0));
        //var newE2 = EdgeUtility.connectEdgeToEdges(newE,)
    }

    @Test
    public void testConnectTwoInCycle() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(3,0))
                .Vertex(new Vertex(4,1))
                .Vertex(new Vertex(3,1))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(1,0),new Vertex(1,1))
                .addHalfEdge(new Vertex(3,0),new Vertex(3,1));

        builder.removeHalfEdge(new Vertex(1,0),new Vertex(3,0));
        HalfEdge e1 = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        EdgeUtility.connectWithBothInCycles(builder.D.getEdge(new Vertex(1,0),new Vertex(1,1))
            , builder.D.getEdge(new Vertex(3,0),new Vertex(3,1)),e1,eTwin);
        Assertions.assertTrue(builder.D.hasEdge(new Vertex(1,0),new Vertex(3,0)));

    }
}
