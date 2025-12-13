package test;

import core.BowyerWatsonDT;
import core.BowyerWatsonDT2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.*;
import utility.graph.Graph2DTopologyBuilder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BowyerWatsonDTTest {

    @Test
    void removeTriangle() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(1,0))
                .Vertex(new Vertex(1,1))
                .Vertex(new Vertex(0,1))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(1,1));
        var triangles = Test.D.getTriangles();

        assertEquals(2,triangles.size());
        assertEquals(new Triangle(new Vertex(0,0),new Vertex(1,0)
                ,new Vertex(1,1)),triangles.get(0));


        BowyerWatsonDT.removeTriangle(Test,triangles.get(0));
        assertEquals(3,Test.D.vertices.size());

    }

    @Test
    void addTriangle() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(10,0))
                .Vertex(new Vertex(5,10))
                .Vertex(new Vertex(0,0));
        var triangles = Test.D.getTriangles();

        assertEquals(1,triangles.size());
        BowyerWatsonDT.addOneTriangleInside(Test,Test.D.getEdge(new Vertex(0,0),new Vertex(10,0)),
                new Vertex(2.5,5));
        BowyerWatsonDT.addOneTriangleInside(Test,Test.D.getEdge(new Vertex(0,0),new Vertex(5,10)),new Vertex(2.5,5));

        triangles = Test.D.getTriangles();
        assertEquals(3,triangles.size());
    }

    @Test
    void containsEdgeTriangle() {
        HalfEdge.SubdivisionBuilder Test = HalfEdge.SubdivisionBuilder.builder()
                .Vertex(new Vertex(0,0))
                .Vertex(new Vertex(10,0))
                .Vertex(new Vertex(10,10))
                .Vertex(new Vertex(0,10))
                .Vertex(new Vertex(0,0))
                .addHalfEdge(new Vertex(0,0),new Vertex(10,10));
        var list = Test.D.getTriangles();

        assertTrue(BowyerWatsonDT.triangleListContainsEdge(list.get(0),
                Test.D.getEdge(new Vertex(0,0),new Vertex(10,10)),list ));

        assertFalse(BowyerWatsonDT.triangleListContainsEdge(list.get(0),
                Test.D.getEdge(new Vertex(0,0),new Vertex(10,0)),list ));

    }

    @Test
    void compute() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0,0));
        points.add(new Point(10,0));
        points.add(new Point(5,10));

        BowyerWatsonDT.compute(points);

    }

    @Test
    void compute2() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0,0));
        points.add(new Point(10,0));
        points.add(new Point(5,10));

        var builder = BowyerWatsonDT2.compute(points);
        Assertions.assertEquals(3, Graph2DTopologyBuilder.numberOfVertices(builder));
        Assertions.assertEquals(6,Graph2DTopologyBuilder.numberOfEdges(builder));

    }

    @Test
    public void insideCircleTest() {
        Assertions.assertTrue(GeometryUtility.isInsideCircleD(new Point(-100,0),
                new Point(100,0),new Point(0,100),new Point(10,10)));
        Assertions.assertTrue(GeometryUtility.isInsideCircleD(new Point(0,100),new Point(100,0),new Point(-100,0),new Point(10,10)));
        Assertions.assertTrue(GeometryUtility.isInsideCircleD(new Point(100,0),
                new Point(-100,0),new Point(0,100),new Point(10,10)));

    }
}