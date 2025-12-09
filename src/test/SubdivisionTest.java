package test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import utility.*;

import static org.junit.jupiter.api.Assertions.*;

public class SubdivisionTest {



    public static Subdivision createCarre() {
        Subdivision s = new Subdivision();

// Sommets
        Vertex v1 = new Vertex(0,0);
        Vertex v2 = new Vertex(0,1);
        Vertex v3 = new Vertex(1,1);
        Vertex v4 = new Vertex(1,0);

// Faces
        Face f1 = new Face(); // face intérieure
        Face fOuter = new Face(); // face infinie / extérieure

// Demi-arêtes (half-edges)
        HalfEdge edge1 = new HalfEdge();
        HalfEdge edge2 = new HalfEdge();
        HalfEdge edge3 = new HalfEdge();
        HalfEdge edge4 = new HalfEdge();
        HalfEdge twin1 = new HalfEdge();
        HalfEdge twin2 = new HalfEdge();
        HalfEdge twin3 = new HalfEdge();
        HalfEdge twin4 = new HalfEdge();

// ---- Configuration des sommets ----
        edge1.v = v1;
        edge2.v = v2;
        edge3.v = v3;
        edge4.v = v4;

        twin1.v = v2;
        twin2.v = v3;
        twin3.v = v4;
        twin4.v = v1;

// Associer une demi-arête incidente à chaque sommet
        v1.incidentEdge = edge1;
        v2.incidentEdge = edge2;
        v3.incidentEdge = edge3;
        v4.incidentEdge = edge4;

// ---- Configuration des arêtes internes (face f1) ----
        edge1.next = edge2;
        edge1.prev = edge4;
        edge1.twin = twin1;
        edge1.incidentFace = f1;
        edge2.next = edge3;
        edge2.prev = edge1;
        edge2.twin = twin2;
        edge2.incidentFace = f1;
        edge3.next = edge4;
        edge3.prev = edge2;
        edge3.twin = twin3;
        edge3.incidentFace = f1;
        edge4.next = edge1;
        edge4.prev = edge3;
        edge4.twin = twin4;
        edge4.incidentFace = f1;

// ---- Configuration des twins (face extérieure fOuter) ----
        twin1.next = twin4;
        twin1.prev = twin2;
        twin1.twin = edge1;
        twin1.incidentFace = fOuter;
        twin2.next = twin1;
        twin2.prev = twin3;
        twin2.twin = edge2;
        twin2.incidentFace = fOuter;
        twin3.next = twin2;
        twin3.prev = twin4;
        twin3.twin = edge3;
        twin3.incidentFace = fOuter;
        twin4.next = twin3;
        twin4.prev = twin1;
        twin4.twin = edge4;
        twin4.incidentFace = fOuter;

// ---- Affecter un half-edge à chaque face ----
        f1.outerComponent = edge1;
        fOuter.outerComponent = twin1;

        fOuter.innerComponents.add(edge1);
// ---- Ajouter à la subdivision ----
        s.vertices.add(v1);
        s.vertices.add(v2);
        s.vertices.add(v3);
        s.vertices.add(v4);
        s.halfEdges.add(edge1);
        s.halfEdges.add(edge2);
        s.halfEdges.add(edge3);
        s.halfEdges.add(edge4);
        s.halfEdges.add(twin1);
        s.halfEdges.add(twin2);
        s.halfEdges.add(twin3);
        s.halfEdges.add(twin4);
        s.faces.add(f1);
        s.faces.add(fOuter);
        return s;
    }

    @Test
    public void testTwinAndEdgeCorresponds() {
        Subdivision Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,-1))   // V1
                .Vertex(new Vertex(3,0))   // V2
                .Vertex(new Vertex(1,1))   // V3
                .Vertex(new Vertex(0,0))
                .build();

        Assert.assertTrue(Subdivision.alltwinandedgecorresponds(Test.halfEdges));

        Assert.assertTrue(Subdivision.outerEdgeBounded(Test.halfEdges));
        Subdivision Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,-1))   // V1
                .Vertex(new Vertex(3,0))   // V2
                .Vertex(new Vertex(1,1))   // V3
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(1,1))
                .build();

        Assert.assertTrue(Subdivision.alltwinandedgecorresponds(Test2.halfEdges));
        Assert.assertTrue(Subdivision.outerEdgeBounded(Test2.halfEdges));
    }

    @Test
    public void testSplitBuilder() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,0))   // V1
                .Vertex(new Vertex(1,1))   // V2
                .Vertex(new Vertex(0,1))   // V3
                .Vertex(new Vertex(0,0));

        Test.splitExistingEdge(new Vertex(0,0),new Vertex(1,0),new Vertex(0.5,0));

        assertTrue(Test.D.hasEdge(new Vertex(0,0),new Vertex(0.5,0)));
        assertTrue(Test.D.hasEdge(new Vertex(0.5,0),new Vertex(1,0)));
        assertFalse(Test.D.hasEdge(new Vertex(0,0),new Vertex(1,0)));

    }

    @Test
    public void testGetTriangles() {
        Subdivision Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(1,1))
                .build();
        var triangles = Test.getTriangles();
        assertEquals(2,triangles.size());
        assertEquals(new Triangle(Test.vertices.get(0),Test.vertices.get(1),Test.vertices.get(2)),triangles.get(0));
        assertEquals(new Triangle(Test.vertices.get(2),Test.vertices.get(3),Test.vertices.get(0)),
                triangles.get(1));
        Subdivision Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(-1,1))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(1,1))
                .build();
        var triangles2 = Test2.getTriangles();
        assertEquals(1,triangles2.size());
        assertEquals(new Triangle(Test.vertices.get(0),Test.vertices.get(1),Test.vertices.get(2)),triangles2.get(0));

    }

    @Test
    public void testRemoveEdge() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,-1))   // V1
                .Vertex(new Vertex(3,0))   // V2
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,0));

        Test.removeHalfEdge(new Vertex(0,0),new Vertex(1,-1));
        //HalfEdge.removeEdge(Test.D,Test.getVertex(new Vertex(0,0)), Test.getVertex(new Vertex(1,-1)));
        assertFalse(Test.D.hasEdge(new Vertex(0,0),new Vertex(1,-1)));
        assertFalse(Test.getVertex(new Vertex(1,-1)).incidentEdge.prev.v.equals(new Vertex(0,0)));
        Test.removeHalfEdge(new Vertex(0,0),new Vertex(1,1));
        assertFalse(Test.getVertex(new Vertex(1,1)).incidentEdge.next.v.equals(new Vertex(0,0)));


    }

    @Test
    public void removeMultipleVertex() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(1,1));
        var list = Test.D.getEdgesAdjacent(new Vertex(0,0));
        assertEquals(6,list.size());
        Test.removeVertex(new Vertex(0,0));
        assertFalse(Test.getVertex(new Vertex(1,1)).incidentEdge.prev.v.equals(new Vertex(0,0)));
        Test.removeVertex(new Vertex(1,0));
        assertFalse(Test.getVertex(new Vertex(1,1)).incidentEdge.prev.v.equals(new Vertex(1,0)));
    }

    @Test
    public void isFaceIn() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                ;

        assertTrue(Test.D.isVertexInFace(Test.D.faces.getFirst(),new Vertex(0.5,0.5)));
        assertFalse(Test.D.isVertexInFace(Test.D.faces.getFirst(),new Vertex(3,0.5)));
    }
    @Test
    public void testRemoveDiagonal() {
        Subdivision Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                .build();
        HalfEdge.insertDiagonal(Test,Test.halfEdges.get(0).v,Test.halfEdges.get(4).v);
        assertTrue(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));
        HalfEdge.removeDiagonal(Test,Test.halfEdges.get(8),Test.halfEdges.get(9));
        assertFalse(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));
        HalfEdge.insertDiagonal(Test,Test.halfEdges.get(4).v,Test.halfEdges.get(0).v);
        assertTrue(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));
        HalfEdge.removeDiagonal(Test,Test.halfEdges.get(9),Test.halfEdges.get(8));
        assertFalse(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));

        HalfEdge.insertDiagonal(Test,Test.halfEdges.get(0).v,Test.halfEdges.get(4).v);
        assertTrue(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));
        HalfEdge.removeDiagonal(Test,Test.halfEdges.get(8).v,Test.halfEdges.get(9).v);
        assertFalse(Test.hasEdge(new Vertex(0,0),new Vertex(1,1)));
    }

    @Test
    public void removeVertex() {
        HalfEdge.SubdivisionBuilder Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,0))   // V1
                .Vertex(new Vertex(1,1))   // V2
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0));

        Test2.removeVertex(new Vertex(0,0));
        assertFalse(Test2.D.hasEdge(new Vertex(0,0),new Vertex(1,0)));
        assertFalse(Test2.getVertex(new Vertex(1,0)).incidentEdge.prev.v.equals(new Vertex(0,0)));
        assertFalse(Test2.getVertex(new Vertex(0,1)).incidentEdge.next.v.equals(new Vertex(0,0)));
    }

    @Test
    public void testCyclic() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,-1))   // V1
                .Vertex(new Vertex(3,0))   // V2
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,0));

        assertTrue(HalfEdge.isCyclic(Test.D,Test.D.halfEdges.get(1)));
        Test.removeHalfEdge(new Vertex(1,-1),new Vertex(0,0));
        assertFalse(Test.D.hasEdge(new Vertex(0,0),new Vertex(1,-1)));

        assertFalse(HalfEdge.isCyclic(Test.D,Test.D.halfEdges.get(1)));

        HalfEdge.SubdivisionBuilder Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,0))   // V1
                .Vertex(new Vertex(1,1))   // V2
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0));

        Test2.addHalfEdge(new Vertex(0,0),new Vertex(1,1));
        Test2.removeHalfEdge(new Vertex(0,0),new Vertex(0,1));
        assertFalse(Test2.D.hasEdge(new Vertex(0,0),new Vertex(0,1)));
        assertTrue(HalfEdge.isCyclic(Test2.D,Test2.D.halfEdges.get(1)));
        assertFalse(HalfEdge.isCyclic(Test2.D,Test2.D.halfEdges.get(0)));
    }

    @Test
    public void getVertexInRIghtOrder() {
        HalfEdge.SubdivisionBuilder Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,0))   // V1
                .Vertex(new Vertex(1,1))   // V2
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0));

        var result = Test2.getVerticesInRightOrder(new Vertex(0,1),new Vertex(0,0));
        var list = result.stream().toList();

        assertSame(list.get(1).incidentEdge.next.v,list.get(0));

         result = Test2.getVerticesInRightOrder(new Vertex(1,0),new Vertex(0,0),new Vertex(1,1));
         list = result.stream().toList();
    }

    public void completeEdge() {
        HalfEdge.SubdivisionBuilder Test2 = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))   // V0
                .Vertex(new Vertex(1,0))   // V1
                .Vertex(new Vertex(1,1))   // V2
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0));
    }

}