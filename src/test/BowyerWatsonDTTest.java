package test;

import core.BowyerWatsonDT;
import core.BowyerWatsonDT2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.*;
import utility.graph.Graph2DTopologyBuilder;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
    void compute2() throws InterruptedException {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0,0));
        points.add(new Point(10,0));
        points.add(new Point(5,10));

        var builder = BowyerWatsonDT2.compute(points);
        Assertions.assertEquals(1,builder.size());
        //Assertions.assertEquals(3, Graph2DTopologyBuilder.numberOfVertices(builder));
        //Assertions.assertEquals(6,Graph2DTopologyBuilder.numberOfEdges(builder));
        //builder.showGraph();
       // new CountDownLatch(2).await();
    }

    @Test
    void computeComplex() throws InterruptedException {
        ArrayList<Point> points = new ArrayList<>();

        // Enveloppe convexe
        points.add(new Point(0, 0));
        points.add(new Point(20, 0));
        points.add(new Point(25, 10));
        points.add(new Point(15, 25));
        points.add(new Point(0, 20));

        // Points intérieurs
        points.add(new Point(8, 6));
        points.add(new Point(12, 8));
        points.add(new Point(10, 15));
        points.add(new Point(6, 12));

        // Points presque cocycliques (tests numériques)
        points.add(new Point(13, 13));
        points.add(new Point(14, 12));
        points.add(new Point(12, 14));

        var result = BowyerWatsonDT2.compute(points);
        System.out.println(result);
        //result.showGraph();
        //new CountDownLatch(10).await();
    }

    @Test
    void computeCarre() throws InterruptedException {
        ArrayList<Point> points = new ArrayList<>();

        // Carré
        points.add(new Point(0, 0));
        points.add(new Point(10, 0));
        points.add(new Point(10, 10));
        points.add(new Point(0, 10));

        // Point central
        points.add(new Point(2.5, 2.5));
        var result = BowyerWatsonDT2.compute(points);
        Assertions.assertEquals(4,result.size());
        //result.showGraph();
        //new CountDownLatch(10).await();
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