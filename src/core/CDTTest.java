package core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
class CDTTest {

    @Test
    void compute() {
        ArrayList<Point> points = new ArrayList<>();

        // Carré
        points.add(new Point(0, 0));
        points.add(new Point(10, 0));
        points.add(new Point(10, 10));
        points.add(new Point(0, 10));

        // Point central
        points.add(new Point(4, 2.5));
        var builder = CDT.compute(points);

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }
        CDT.ConstraintPolygon constraintPolygon = new CDT.ConstraintPolygon();
        ArrayList<Segment<Vertex>> contrainst = new ArrayList<>();
        contrainst.add(new Segment<>(new Vertex(0,0),new Vertex(10,10)));
        constraintPolygon.segments = contrainst;
        CDT.applyConstraints(List.of(constraintPolygon),builder);
    }

    @Test
    void  compute2() {
        ArrayList<Point> points = new ArrayList<>();

        // Carré
        points.add(new Point(0, 0));
        points.add(new Point(100, 0));
        points.add(new Point(100, 100));
        points.add(new Point(0, 100));

        // Point central
        points.add(new Point(10, 10));
        points.add(new Point(15,10));
        points.add(new Point(15,15));
        points.add(new Point(10,15));
        var builder = CDT.compute(points);

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }


        ArrayList<Segment<Vertex>> contrainst = new ArrayList<>();
        contrainst.add(new Segment<>(new Vertex(10,10),new Vertex(15,10)));
        contrainst.add(new Segment<>(new Vertex(15,10),new Vertex(15,15)));
        contrainst.add(new Segment<>(new Vertex(15,15),new Vertex(10,15)));
        contrainst.add(new Segment<>(new Vertex(10,15),new Vertex(10,10)));
        CDT.ConstraintPolygon constraintPolygon = new CDT.ConstraintPolygon();
        constraintPolygon.segments = contrainst;
        CDT.applyConstraints(List.of(constraintPolygon),builder);
        CDT.removePolygons(builder,List.of(constraintPolygon));

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }
    }

    @Test
    void computeNearPolygon()  {
        ArrayList<Point> points = new ArrayList<>();

        points.add(new Point(17,15));
        points.add(new Point(0, 0));
        points.add(new Point(100, 0));
        points.add(new Point(10,15));
        points.add(new Point(10, 10));
        points.add(new Point(25, 15));
        points.add(new Point(100, 100));
        points.add(new Point(25, 10));
        points.add(new Point(0, 100));

        points.add(new Point(20, 10));
        points.add(new Point(20, 15));

        points.add(new Point(17,10));
        var builder = CDT.compute(points);

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }

        ArrayList<Segment<Vertex>> contrainst = new ArrayList<>();
        contrainst.add(new Segment<>(new Vertex(10,10),new Vertex(17,10)));
        contrainst.add(new Segment<>(new Vertex(17,10),new Vertex(17,15)));
        contrainst.add(new Segment<>(new Vertex(17,15),new Vertex(10,15)));
        contrainst.add(new Segment<>(new Vertex(10,15),new Vertex(10,10)));

        CDT.ConstraintPolygon constraintPolygon = new CDT.ConstraintPolygon();
        constraintPolygon.segments = contrainst;

        ArrayList<Segment<Vertex>> contrainst2 = new ArrayList<>();
        contrainst2.add(new Segment<>(new Vertex(20,10),new Vertex(25,10)));
        contrainst2.add(new Segment<>(new Vertex(25,10),new Vertex(25,15)));
        contrainst2.add(new Segment<>(new Vertex(25,15),new Vertex(20,15)));
        contrainst2.add(new Segment<>(new Vertex(20,15),new Vertex(20,10)));

        CDT.ConstraintPolygon constraintPolygon2 = new CDT.ConstraintPolygon();
        constraintPolygon2.segments = contrainst2;

        CDT.applyConstraints(List.of(constraintPolygon,constraintPolygon2),builder);

        CDT.removePolygons(builder,List.of(constraintPolygon,constraintPolygon2));

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }
    }

    @Test
    void intersectCDT() {
        ArrayList<Point> points = new ArrayList<>();

        points.add(new Point(0, 0));
        points.add(new Point(100, 0));


        points.add(new Point(100, 100));
        points.add(new Point(0, 100));

        points.add(new Point(10,15));
        points.add(new Point(10, 10));
        points.add(new Point(17,15));
        points.add(new Point(17,10));
        points.add(new Point(13,12));
        points.add(new Point(13,8));

        var builder = CDT.compute(points);

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }

        ArrayList<Segment<Vertex>> contrainst = new ArrayList<>();
        contrainst.add(new Segment<>(new Vertex(10,10),new Vertex(17,10)));
        contrainst.add(new Segment<>(new Vertex(17,10),new Vertex(17,15)));
        contrainst.add(new Segment<>(new Vertex(17,15),new Vertex(10,15)));
        contrainst.add(new Segment<>(new Vertex(10,15),new Vertex(10,10)));

        CDT.ConstraintPolygon constraintPolygon = new CDT.ConstraintPolygon();
        constraintPolygon.segments = contrainst;
        CDT.applyConstraints(List.of(constraintPolygon),builder);
        CDT.removePolygons(builder,List.of(constraintPolygon));

        for(Face f : builder.faces.keySet()) {
            Assertions.assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        }
    }
}