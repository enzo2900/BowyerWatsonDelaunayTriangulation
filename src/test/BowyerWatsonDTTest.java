package test;

import core.BowyerWatsonDT;
import org.junit.jupiter.api.Test;
import utility.HalfEdge;
import utility.Subdivision;
import utility.Triangle;
import utility.Vertex;

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


        BowyerWatsonDT.removeTriangle(Test.D,triangles.get(0));
        assertEquals(3,Test.D.vertices.size());

    }
}