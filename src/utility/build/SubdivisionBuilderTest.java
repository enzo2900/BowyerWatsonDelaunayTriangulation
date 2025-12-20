package utility.build;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static utility.build.SubdivisionBuilder.*;

class SubdivisionBuilderTest {

    @Test
    public void create() {
        var builder = SubdivisionBuilder.builder();
        builder.buildVertex(new Vertex(0,0),new Vertex(1,0))
                .buildVertex(new Vertex(1,0),new Vertex(1,1))
                .buildVertex(new Vertex(1,1),new Vertex(0,0))
                .buildVertex(new Vertex(1,0),new Vertex(2,1))
                .buildVertex(new Vertex(2,1),new Vertex(1,1))
                .buildVertex(new Vertex(0,0),new Vertex(-1,0))
                .buildVertex(new Vertex(-1,0),new Vertex(-1,1))
                .buildVertex(new Vertex(-1,1),new Vertex(0,0))
                .buildVertex(new Vertex(1,1),new Vertex(0,1))
                .buildVertex(new Vertex(0,1),new Vertex(0,0));

        var list = builder.vertices.values().stream().toList();
        ArrayList<HalfEdge> array = new ArrayList<>();
        list.forEach(e->e.forEach(h -> {
            if(!array.contains(h)) {
                array.add(h);
            }
        }));
        Assertions.assertTrue(Subdivision.outerEdgeBounded(array));
        //Assertions.assertTrue(builder().);
        //Assertions.assertTrue(Subdivision.inEdgeBounded(array));
    }

    @Test
    public void createPolygon() {
        var builder = SubdivisionBuilder.builder();
        ArrayList<Vertex> p1 = new ArrayList<>();
        p1.add(new Vertex(0,0));
        p1.add(new Vertex(1,0));
        p1.add(new Vertex(1,1));
        p1.add(new Vertex(0,0));
        Face f=builder.buildPolygon(p1);
        Assertions.assertEquals(6,builder.faces.get(f).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        assertTrue(Subdivision.outerEdgeBounded(builder.faces.get(f)));
        builder = SubdivisionBuilder.builder();
        p1.clear();
        p1.add(new Vertex(0,0));
        p1.add(new Vertex(-1,0));
        p1.add(new Vertex(-1,1));
        p1.add(new Vertex(0,0));
        f=builder.buildPolygon(p1);
        Assertions.assertEquals(6,builder.faces.get(f).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        assertTrue(Subdivision.outerEdgeBounded(builder.faces.get(f)));

        ArrayList<Vertex> p2 = new ArrayList<>();
        p2.add(new Vertex(-1,0));
        p2.add(new Vertex(-2,1));
        p2.add(new Vertex(-1,1));
        p2.add(new Vertex(-1,0));

        var f2 = builder.buildPolygon(p2);
        Assertions.assertEquals(6,builder.faces.get(f2).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f2)));
//        assertTrue(Subdivision.outerEdgeBounded(builder.faces.get(f)));

        p1.clear();
        p1.add(new Vertex(0,0));
        p1.add(new Vertex(1,0));
        p1.add(new Vertex(1,1));
        p1.add(new Vertex(0,0));
        var f3=builder.buildPolygon(p1);
        Assertions.assertEquals(6,builder.faces.get(f3).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f3)));
        Assertions.assertEquals(3,builder.faces.size());

        p1.clear();
        p1.add(new Vertex(0,2));
        p1.add(new Vertex(1,2));
        p1.add(new Vertex(1,3));
        p1.add(new Vertex(0,2));
        var f4=builder.buildPolygon(p1);
        Assertions.assertEquals(6,builder.faces.get(f3).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f4)));

        p1.clear();
        p1.add(new Vertex(0,0));
        p1.add(new Vertex(0,2));
        p1.add(new Vertex(1,1));
        p1.add(new Vertex(0,0));
        var f5 = builder.buildPolygon(p1);
        Assertions.assertEquals(6,builder.faces.get(f3).size());
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f4)));
        /*builder.buildVertex(new Vertex(0,0),new Vertex(0,2));
        builder.buildVertex(new Vertex(0,2),new Vertex(1,1));*/
        var triangles = builder.getTriangles();
        assertEquals(5,triangles.size());
    }

    public void buildTriangulation() {

    }

    @Test
    public void isCWPolygon() {
        ArrayList<Vertex> polygon = new ArrayList<>();
        polygon.add(new Vertex(0,0));
        polygon.add(new Vertex(0,2));
        polygon.add(new Vertex(1,1));
        polygon.add(new Vertex(0,0));
        assertTrue(SubdivisionBuilder.isCWPolygon(polygon));

        polygon.clear();
        polygon.add(new Vertex(0,0));
        polygon.add(new Vertex(-1,1));
        polygon.add(new Vertex(3,3));
        polygon.add(new Vertex(1,1));
        polygon.add(new Vertex(0,0));
        assertTrue(SubdivisionBuilder.isCWPolygon(polygon));

        polygon.clear();

        polygon.add(new Vertex(0,0));
        polygon.add(new Vertex(3,1));
        polygon.add(new Vertex(1,2));
        polygon.add(new Vertex(0,0));

        assertFalse(SubdivisionBuilder.isCWPolygon(polygon));
        var reversedPolygon = polygon.reversed();
        assertTrue(SubdivisionBuilder.isCWPolygon(reversedPolygon));
    }

    @Test
    public void splitFace() {
        var builder = SubdivisionBuilder.builder();
        ArrayList<Vertex> p= new ArrayList<>();
        p.add(new Vertex(0,0));
        p.add(new Vertex(1,0));
        p.add(new Vertex(1,1));
        p.add(new Vertex(0,1));
        p.add(new Vertex(0,0));
        var f = builder.buildPolygon(p);
        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(f)));
        var list = builder.faces.get(f);

        var edges = builder.splitFace(f,list.get(0),list.get(4));
        assertEquals(edges.left().v,new Vertex(1,1));
        assertEquals(edges.left().twin.v,new Vertex(0,0));
        assertEquals(2,builder.faces.keySet().size());
    }

    @Test
    public void mergeFace() {
        var builder = SubdivisionBuilder.builder();
        ArrayList<Vertex> p= new ArrayList<>();
        p.add(new Vertex(0,0));
        p.add(new Vertex(1,0));
        p.add(new Vertex(1,1));
        p.add(new Vertex(0,0));

        var f = builder.buildPolygon(p);

        p.clear();
        p.add(new Vertex(0,0));
        p.add(new Vertex(1,1));
        p.add(new Vertex(0,1));
        p.add(new Vertex(0,0));

        var f2 = builder.buildPolygon(p);
        var list = builder.faces.get(f2);
        var newF = builder.mergeFaces(f,f2,list.get(0),list.get(1));

        assertTrue(Subdivision.inEdgeBounded(builder.faces.get(newF)));

    }
}