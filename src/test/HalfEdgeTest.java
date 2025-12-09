package test;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.HalfEdge;
import utility.Polygon;
import utility.Subdivision;
import utility.Vertex;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HalfEdgeTest {

    @Test
    void insertDiagonal() {
        Subdivision S = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                        .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0)).build();
        int e1 = 2;
        ArrayList<Vertex> vertices = S.vertices;
        HalfEdge.insertDiagonal(S, vertices.get(0), vertices.get(e1));

        Assert.assertEquals(vertices.get(0).incidentEdge.next.v, vertices.get(e1));
        Assert.assertEquals(vertices.get(2).incidentEdge.prev.v, vertices.get(1));
        Assert.assertEquals(vertices.get(2).incidentEdge.next.v, vertices.get(0));
        // Index 5 and 4 = new Vertex created;
        Assert.assertEquals(vertices.get(0).incidentEdge.next.v, vertices.get(2));
        Assert.assertEquals(vertices.get(0).incidentEdge.prev.v, vertices.get(3));
        Assert.assertEquals(vertices.get(0).incidentEdge.next.next.v, vertices.get(3));
        Assert.assertEquals(vertices.get(0),S.halfEdges.get(1).next.v);
        Assert.assertEquals(vertices.get(3),S.halfEdges.get(1).next.next.v);
        Assert.assertEquals(vertices.get(0),S.halfEdges.get(7).v);
        int e6 = 10;


        ArrayList<Polygon> polygonList = Subdivision.findNewFacesFromDiagonals(S);

        assertEquals(2,polygonList.size());
        Vertex.resetCounter();
        Subdivision S2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,1))   // V1
                .Vertex(new Vertex(1,0))   // V2
                .Vertex(new Vertex(2,1))   // V3
                .Vertex(new Vertex(2,0))   // V4
                .Vertex(new Vertex(3,1))
                .Vertex(new Vertex(3,2))
                .Vertex(new Vertex(0,2))
                .Vertex(new Vertex(0,0))
                .build();

        HalfEdge.insertDiagonal(S2,S2.vertices.get(1),S2.vertices.get(6));
        assertEquals(S2.vertices.get(6),S2.vertices.get(1).incidentEdge.next.v);
        HalfEdge.insertDiagonal(S2,S2.vertices.get(1),S2.vertices.get(3));

        assertEquals(S2.vertices.get(0).incidentEdge.next.v, S2.vertices.get(1));
        assertEquals(S2.vertices.get(1),S2.vertices.get(2).incidentEdge.next.next.v);
        assertEquals(S2.vertices.get(1),S2.vertices.get(5).incidentEdge.next.next.v);
        assertEquals(S2.vertices.get(3),S2.vertices.get(5).incidentEdge.next.next.next.v);
        //polygonList = Subdivision.findNewFacesFromDiagonals(S2);

        //assertEquals(3,polygonList.size());





    }

    @Test
    public void buildPolygon() {
        Subdivision Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0)).build();

        Assertions.assertEquals(Test.vertices.get(0).incidentEdge.next.v, Test.vertices.get(1));
        Assertions.assertEquals( Test.vertices.get(1),Test.vertices.get(1).incidentEdge.twin.next.v);
        Assertions.assertEquals( Test.vertices.get(2),Test.vertices.get(1).incidentEdge.twin.v);
        Assertions.assertEquals( Test.vertices.get(1),Test.vertices.get(0).incidentEdge.twin.v);
    }

    @Test
    public void testRemoveDiagonal() {
        Subdivision Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0)).addHalfEdge(new Vertex(0,0),new Vertex(1,1)).build();
        ArrayList<Vertex> vertices = Test.vertices;
        Assert.assertEquals(vertices.get(1),Test.halfEdges.get(1).v);
        Assert.assertEquals(vertices.get(2),Test.halfEdges.get(1).prev.v);
        Assert.assertEquals(vertices.get(0),Test.halfEdges.get(1).next.v);
        Assert.assertEquals(vertices.get(3),Test.halfEdges.get(1).next.next.v);
        Assert.assertEquals(vertices.get(0),Test.halfEdges.get(7).v);

        HalfEdge.removeDiagonal(Test,Test.halfEdges.get(8),Test.halfEdges.get(9));
        Assert.assertEquals(vertices.get(1),Test.halfEdges.get(1).v);
        Assert.assertEquals(vertices.get(2),Test.halfEdges.get(1).prev.v);
        Assert.assertEquals(vertices.get(0),Test.halfEdges.get(1).next.v);
        Assert.assertEquals(vertices.get(3),Test.halfEdges.get(1).next.next.v);
        Assert.assertEquals(vertices.get(0),Test.halfEdges.get(7).v);
        assertTrue(!Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));

    }

    @Test
    public void testBuilder() {
        HalfEdge.SubdivisionBuilder builder = HalfEdge.SubdivisionBuilder.builder();
        builder.Vertex(new Vertex(0,0));
        ArrayList<Vertex> vertices = builder.D.vertices;
        ArrayList<HalfEdge> halfEdges = builder.D.halfEdges;
        assertEquals(1, vertices.size());

        assertEquals(0, halfEdges.size());
        builder.Vertex(new Vertex(1,0));
        assertEquals(2, vertices.size());
        assertEquals(2, halfEdges.size());

        assertEquals(vertices.get(0), halfEdges.get(0).v);
        assertEquals(vertices.get(1), halfEdges.get(1).v);

        builder.Vertex(new Vertex(1,1));
        assertEquals(3, vertices.size());
        assertEquals(4, halfEdges.size());
        assertEquals(vertices.get(1), halfEdges.get(2).v);
        assertEquals(vertices.get(2), halfEdges.get(3).v);
        assertEquals(vertices.get(1), halfEdges.get(0).next.v);
        assertTrue(vertices.get(2) == halfEdges.get(1).prev.v);
        assertTrue(vertices.get(1) == halfEdges.get(3).next.v);

        builder.Vertex(new Vertex(0,1));
        assertEquals(4, vertices.size());
        assertEquals(6, halfEdges.size());

        assertTrue(vertices.get(3) == halfEdges.get(3).prev.v);
        assertTrue(vertices.get(2) == halfEdges.get(5).next.v);
        assertTrue(vertices.get(3) == halfEdges.get(5).v);
        builder.Vertex(new Vertex(0,0));
        assertEquals(4, vertices.size());
        assertEquals(8, halfEdges.size());
        assertTrue(vertices.get(0) == halfEdges.get(6).next.v);
        assertSame(vertices.get(2), halfEdges.get(6).prev.v);
        assertTrue(vertices.get(3) == halfEdges.get(6).v);
        assertTrue(vertices.get(0) == halfEdges.get(7).v);
        assertTrue(vertices.get(3) == halfEdges.get(7).next.v);
        assertTrue(vertices.get(1) == halfEdges.get(7).prev.v);
        assertTrue(vertices.get(0) == halfEdges.get(1).next.v);
        assertTrue(vertices.get(1) == halfEdges.get(1).v);
        assertTrue(vertices.get(3) == halfEdges.get(0).prev.v);


        assertTrue(builder.D.faces.get(0) == halfEdges.get(0).incidentFace);
        assertTrue(builder.D.faces.get(0) == halfEdges.get(2).incidentFace);
        assertTrue(builder.D.faces.get(0) == halfEdges.get(4).incidentFace);
        assertTrue(builder.D.faces.get(0) == halfEdges.get(6).incidentFace);

        assertTrue(null == halfEdges.get(1).incidentFace);
        //assertEquals(vertices.get(0), halfEdges.get(1).prev.v);
    }
}