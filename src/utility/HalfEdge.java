package utility;

import java.util.*;

public class HalfEdge implements MapPoint {

    public Vertex v;

    public HalfEdge twin;

    public HalfEdge next;
    public HalfEdge prev;
    public Face incidentFace;

    public int tag;


    public double magnitude() {
        double x = twin.v.x - v.x;
        double y = twin.v.y - v.y;
        return Math.sqrt(x * x  + y * y);
    }
    public double[] normalized() {
        double magnitude = magnitude();
        double x = twin.v.x - v.x;
        double y = twin.v.y - v.y;
        return new double[] {x / magnitude,y / magnitude};
    }

    public static ArrayList<HalfEdge> copyStatic(ArrayList<HalfEdge> edges,ArrayList<Face> faces
            ,ArrayList<Face> faceThatWillBeCopied,ArrayList<Vertex> vertices, ArrayList<Vertex> verticesToCopy,Subdivision D
    ,HashMap<Vertex,ArrayList<Face>> faceMap) {

        ArrayList<HalfEdge> edges1 = new ArrayList<>();
        Map<HalfEdge, HalfEdge> copyMap = new HashMap<>();
        Map<Face,Face> faceFaceMap = new HashMap<>();
        Map<Vertex,Vertex> vertexMap = new HashMap<>();

        for(HalfEdge e : edges) {
            HalfEdge copy = new HalfEdge();
            copyMap.put(e, copy);

        }
        for(Vertex v: vertices) {
            Vertex vertexCopied = new Vertex(v.x,v.y);
            vertexMap.put(v,vertexCopied);
            vertexCopied.incidentEdge = copyMap.get(v.incidentEdge);


            verticesToCopy.add(vertexCopied);
        }

        for(Face f : faces) {
            Face fCopied = new Face();
            faceFaceMap.put(f,fCopied);
            faceThatWillBeCopied.add(fCopied);

            fCopied.outerComponent = copyMap.get(f.outerComponent);
            for(HalfEdge e : f.innerComponents) {
                fCopied.innerComponents.add(copyMap.get(e));
            }
        }







        for(HalfEdge e : edges) {
            HalfEdge copy = copyMap.get(e);

            Vertex vCopy = vertexMap.get(e.v);

            copy.v = vCopy; // ou copier le vertex si nécessaire

            copy.incidentFace = faceFaceMap.get(e.incidentFace); // ou créer une copie de la face
            copy.next = copyMap.get(e.next);
            copy.prev = copyMap.get(e.prev);
            copy.twin = copyMap.get(e.twin);
            copy.tag = e.tag;
            edges1.add(copy);

        }
        for(HalfEdge edge : edges1) {
            Vertex vCopy = edge.v;
            ArrayList<Face> faces1 = faceMap.getOrDefault(vCopy,new ArrayList<>());
            Face f = edge.incidentFace;
            if(f == null) {
                continue;
            }
            if(!faces1.contains(f)) {
                faces1.add(f);
            }
            faceMap.put(vCopy,faces1);
        }

        for(Vertex v : verticesToCopy) {
            HalfEdge found = HalfEdge.findFrom(edges1.getFirst(), v);
            if(found != null) {
                v.incidentEdge = found;
            }


        }

        return edges1;

    }

    public double getXatY(double ySweep) {
        MapPoint start = this;
        MapPoint end = twin;
        if (start.getY() == end.getY()) return Math.min(start.getX(), end.getX()); // segment horizontal
        return start.getX() + (ySweep - start.getY()) * (end.getX() - start.getX()) / (end.getY() - start.getY());
    }

    public double getXNormal() {

        MapPoint start = this;
        MapPoint end = twin;
        double yDirection = end.getY() -end.getY();
        if(isToTheRight()) {
            return yDirection;
        }
        return -yDirection;
    }

    public boolean isToTheRight() {
        double bx = v.x, by = v.y;
        Vertex nextV = v.incidentEdge.next.v;
        Vertex prevV = v.incidentEdge.prev.v;


        if(prevV.y > v.y ) {
            return true;
        } else if(prevV.y == v.y ) {
            return true;
        } else {
            return false;
        }
    }

    public double getYNormal(){
        MapPoint start = this;
        MapPoint end = twin;
        double xDirection = end.getX() -end.getX();
        return -xDirection;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof HalfEdge edge) {
            return v.equals(edge.v) && next.v.equals(edge.twin.v);
        }
        return false;
    }

    @Override
    public double getX() {
        return v.x;
    }

    @Override
    public double getY() {
        return v.y;
    }


    /**
     * The vertex must be build in the antihorary sense
     */
    public static class SubdivisionBuilder {
        public Subdivision D;
        Face polygonInnerFace;
        Vertex currentVertex ;

        boolean reachedFirstVertex;
        HalfEdge firstHalfEdge;
        Vertex firstVertex;
        HalfEdge prevHalfEdge;

        public SubdivisionBuilder() {
            D = new Subdivision();
            polygonInnerFace = new Face();
            reachedFirstVertex = false;
        }

        public static SubdivisionBuilder builder() {

            return new SubdivisionBuilder();
        }
        public Subdivision build() {
            return D;
        }


        public Vertex getVertex(Vertex v) {
            List<Vertex> vertexList = D.vertices.stream().filter((edge)-> {
                return edge.equals(v);
            }).toList();
            if(vertexList.isEmpty()) {
                return null;
            }
            return vertexList.get(0);
        }

        public ArrayList<Vertex> getVerticesInRightOrder(Vertex... vertices) {
            var list =  D.vertices.stream().filter((edge)-> {
                return Arrays.stream(vertices).anyMatch(v -> edge.equals(v));
            }).toList();
            if(list.size() != vertices.length) {
                throw new RuntimeException("Erreur pas la meme taille");
            }
            return swapVertices(list);
        }

        public double distanceToTheAntiH(HalfEdge edge, HalfEdge toFind) {
            return 0;
        }

        public ArrayList<Vertex> swapVertices(List<Vertex> vertices) {
            ArrayList<Vertex> swapped = new ArrayList<>();
            if(vertices.size() == 1) {
                swapped.add(vertices.getFirst());
            } else if(vertices.size() == 2) {
                var v1 = vertices.get(0);
                var v2 = vertices.get(1);
                var next = v2.incidentEdge.prev.v;
                var prev = v1.incidentEdge.next.v;
                var SameV2 = v2.incidentEdge.next.v.equals(v1) && v2.incidentEdge.prev.v.equals(v1);
                var SameV1 = v1.incidentEdge.next.v.equals(v2) && v1.incidentEdge.prev.v.equals(v2);
                if(!(v1.equals(next) || v2.equals(prev)) || (SameV1 && (!SameV2 && v2.incidentEdge.next.v.equals(v1)))) {
                    swapped.add(v2);
                    swapped.add(v1);


                } else {

                    swapped.add(v1);
                    swapped.add(v2);

                }
            } else {
                int split = vertices.size();
                var list1 = vertices.subList(0,2);
                var list2 = vertices.subList(2,vertices.size());
                swapped.addAll(swapVertices(list1));
                swapped.addAll(swapVertices(list2));
            }
            return swapped;


        }

        /**
         * Split an existing Edge into two new edges. Split it with Vertex splitBetween
         * @param to
         * @param from
         * @param splitBetween
         * @return
         */
        public SubdivisionBuilder splitExistingEdge(Vertex to, Vertex from, Vertex splitBetween) {
            Vertex finalV = to;
            Vertex finalV1 = from;
            List<Vertex> vertexList = D.vertices.stream().filter((edge)-> {
                return edge.equals(finalV) || edge.equals(finalV1);
            }).toList();

            if(vertexList.size() != 2) {
                System.out.println("No vertex corresponding to v1 or v2." );
                return this;
            }

            var list = getVerticesInRightOrder(to,from);
            to = list.get(0);
            from = list.get(1);

            HalfEdge.removeEdge(D,to,from);
            addIncompleteEdgeFrom(to,splitBetween);
            addHalfEdge(from,splitBetween);
            return this;
        }
        public SubdivisionBuilder Vertex(Vertex vertex) {
            if(reachedFirstVertex) return this;
            HalfEdge c ;

            if(currentVertex != null) {

                HalfEdge e1 = new HalfEdge();

                HalfEdge e1Twin = new HalfEdge();
                e1.incidentFace = polygonInnerFace;
                currentVertex.incidentEdge = e1;
                e1.v = currentVertex;
                e1Twin.v = vertex;
                e1.twin = e1Twin;
                e1Twin.twin = e1;
                if(firstHalfEdge == null) {
                    firstHalfEdge = e1;

                }
                if(prevHalfEdge != null) {
                    prevHalfEdge.next = e1;
                    prevHalfEdge.twin.prev = e1Twin;
                    e1.prev = prevHalfEdge;
                    e1Twin.next = prevHalfEdge.twin;

                }
                prevHalfEdge = e1;
                c = e1;

                D.halfEdges.add(e1);
                D.halfEdges.add(e1Twin);

                if(vertex.equals(firstVertex)) {
                    firstHalfEdge.prev = e1;
                    firstHalfEdge.twin.next = e1Twin;
                    e1.next = firstHalfEdge;
                    e1Twin.prev = firstHalfEdge.twin;
                    e1Twin.v = firstVertex;
                    e1Twin.incidentFace = null;
                    reachedFirstVertex = true;
                    polygonInnerFace.outerComponent = firstVertex.incidentEdge;
                    D.faces.add(polygonInnerFace);
                    return this;
                }
            }



            currentVertex = vertex;
            ArrayList<Face> faces = new ArrayList<>();
            faces.add(polygonInnerFace);
            D.vertexMap.put(currentVertex,faces);
            if(firstVertex == null) {
                firstVertex = vertex;
            }
            D.vertices.add(vertex);
            return this;
        }

        /**
         * Prioritize that The first vertex must be right after the second
         * This means that the next vertex of v1 should be v2.
         *
         * @param v1
         * @param v2
         * @return
         */
        public SubdivisionBuilder removeHalfEdge(Vertex v1, Vertex v2) {
           var list = getVerticesInRightOrder(v1,v2);
            v1 = list.get(0);
            v2 = list.get(1);
            HalfEdge.removeEdge(D,v1,v2);
            return this;
        }

        public void removeVertex(Vertex v1) {
            v1 = getVerticesInRightOrder(v1).get(0);
            var list = D.vertexMap.get(v1);
            var listEdges = D.getEdgesAdjacent(v1);
            for(int i = 0 ; i < listEdges.size() ; i +=2) {
                if(D.hasEdge(listEdges.get(i).v,listEdges.get(i+1).v)) {
                    removeHalfEdge(listEdges.get(i).v,listEdges.get(i+1).v);
                }
            }
            D.vertices.remove(v1);
            D.vertexMap.remove(v1);
        }

        public SubdivisionBuilder addCycle(Vertex ... vertices) {
            Face f = new Face();
            D.faces.add(f);
            ArrayList<Vertex> verticesList = new ArrayList<>();
            for(int i = 0 ; i < vertices.length-1 ; i ++) {

                Vertex v = vertices[i];
                Vertex vSub = getVertex(v);
                if(vSub == null) {
                    vSub = v;
                    D.vertices.add(v);
                }

                verticesList.add(vSub);
                var list = D.vertexMap.getOrDefault(vSub,new ArrayList<>());
                list.add(f);
                D.vertexMap.put(vSub,list);
            }
            verticesList.add(vertices[0]);
            for(int i = 0 ; i < verticesList.size()-1 ; i ++) {
                Vertex v1 = verticesList.get(i);
                Vertex v2 = verticesList.get(i+1);
                HalfEdge edge = HalfEdge.insertDiagonal(D,v1,v2);
                edge.incidentFace = f;
                f.outerComponent = edge;
            }
            return this;

        }

        public SubdivisionBuilder addEdge(Vertex v1, Vertex v2) {
            Vertex v1S = getVertex(v1);
            Vertex v2S = getVertex(v2);
            if(v1S == null) {
                v1S = v1;
            }
            if(v2S == null) {
                v2S = v2;
            }

            insertDiagonal(D,v1S,v2S);
            return this;

        }
        public boolean exists(Vertex v) {
            return D.vertices.contains(v);
        }
        public SubdivisionBuilder addHalfEdge(Vertex v1, Vertex v2) {
            if(!reachedFirstVertex) return this;

            Vertex finalV = v1;
            Vertex finalV1 = v2;
            List<Vertex> vertexList = D.vertices.stream().filter((edge)-> {
               return edge.equals(finalV) || edge.equals(finalV1);
            }).toList();

            if(vertexList.size() != 2) {
                System.out.println("No vertex corresponding to v1 or v2." );
                return this;
            }

            v1 = vertexList.get(0);
            v2 = vertexList.get(1);
            insertDiagonal(D,v1,v2);

            return this;
        }
        public SubdivisionBuilder addIncompleteEdgeFrom(Vertex v1,Vertex v2) {
            if(!reachedFirstVertex) return this;

            Vertex finalV = v1;
            Vertex finalV1 = v2;
            List<Vertex> vertexList = D.vertices.stream().filter((edge)-> {
                return edge.equals(finalV);
            }).toList();


            v1 = vertexList.get(0);
            if(v1.incidentEdge == null) {
                insertInitial(D,v1,v2);

            } else {
                insertEdgeNoFace(D,v1,v2);
            }


            return this;
        }

    }

    public static void insertDiagonal2(Subdivision D,Vertex v1, Vertex v2) {
        if(v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return;
        }


        // Créer la diagonale et son twin
        HalfEdge e = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();
        HalfEdge e2 = new HalfEdge();
        HalfEdge eTwin2 = new HalfEdge();

        Vertex V1New = new Vertex(v1.x,v1.y);
        Vertex V2New = new Vertex(v2.x,v2.y);

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;

        e2.v = V2New;
        eTwin2.v = V1New;

        e2.twin = eTwin2;
        eTwin2.twin = e2;


        e2.next = v1.incidentEdge;
        e2.prev = v2.incidentEdge.prev;
        eTwin2.next = v2.incidentEdge.prev.twin;
        eTwin2.prev = v1.incidentEdge.twin;
        e2.next.v = V1New;
        // Relier e et eTwin aux arêtes existantes autour des vertices
        e.prev = v1.incidentEdge.prev;
        e.next = v2.incidentEdge;
        eTwin.prev = v2.incidentEdge.twin;
        eTwin.next = v1.incidentEdge.prev.twin;
        e.prev.next = e;
        e.next.prev = e;


        eTwin.prev.next = eTwin;
        eTwin.next.prev = eTwin;



        e2.next.prev = e2;
        e2.prev.next = e2;
        eTwin2.next.prev = eTwin2;
        eTwin2.prev.next = eTwin2;


        v1.incidentEdge = e;
        v2.incidentEdge = e.next;
        V1New.incidentEdge = e2.next;
        V2New.incidentEdge = e2;

        // Ajouter dans la liste globale d'arêtes si tu en as une
        D.halfEdges .add(e);
        D.halfEdges.add(eTwin);
        D.halfEdges.add(e2);
        D.halfEdges.add(eTwin2);
        D.vertices.add(V1New);
        D.vertices.add(V2New);
    }

    public static void removeDiagonal(Subdivision D,HalfEdge e, HalfEdge eTwin) {
        if (e == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return;
        }


        HalfEdge finalETwin = eTwin;
        List<Face> fMap = D.vertexMap.get(e.v);
        List<Face> fMap2 = D.vertexMap.get(finalETwin.v);
        HalfEdge eNext = e.next;
        HalfEdge ePrev = e.prev;
        HalfEdge eTwinNext = eTwin.next;
        HalfEdge eTwinPrev = eTwin.prev;
        foundEdge:for(Face face : fMap) {
            HalfEdge v1Hf = findFromWhereTwin(face.outerComponent,e.v,eTwin.v);
            if(v1Hf == null) continue ;
            for(Face face1: fMap2) {
                if(face == face1) continue;
                HalfEdge v2Hf = findFromWhereTwin(face1.outerComponent,eTwin.v,e.v);
                if(v2Hf == null) continue ;
                if(v1Hf.twin.v.equals(v2Hf.v) &&v2Hf.twin.v.equals(v1Hf.v)) {
                    e = v1Hf;
                    eTwin = v2Hf;
                    eNext = e.next;
                    ePrev = e.prev;
                    eTwinNext = eTwin.next;
                    eTwinPrev = eTwin.prev;
                    break foundEdge;
                }
            }



        }

        if(!e.twin.v.equals(eTwin.v)  || !eTwin.twin.v.equals(e.v)) {
            throw new RuntimeException("Not correct e or eTwin");

        }


       ePrev.next = eTwinNext;
        eNext.prev = eTwinPrev;
        eTwinPrev.next = eNext;
        eTwinNext.prev = ePrev;
        e.v.incidentEdge = eTwinNext;
        eTwin.v.incidentEdge = eNext;
        /*eNext.twin.next = eTwinPrev;
        ePrev.twin.prev = eTwinNext;
        eTwinNext.twin.next = ePrev;
        eTwinPrev.twin.prev = eNext;*/

        D.faces.remove(e.incidentFace);
        D.faces.remove(eTwin.incidentFace);
        D.halfEdges.remove(e);
        D.halfEdges.remove(eTwin);

        Face f = new Face();
        f.outerComponent = eNext;
        eNext.incidentFace = f;
        ArrayList<Face> faces = D.vertexMap.get(eNext.v);
        faces.remove(e.incidentFace);
        faces.remove(eTwin.incidentFace);
        faces.add(f);
        HalfEdge current = eNext.next;
        while (current != eNext) {
            current.incidentFace = f;
            ArrayList<Face> face = D.vertexMap.get(current.v);
            face.remove(e.incidentFace);
            face.remove(eTwin.incidentFace);

            face.add(f);
            current = current.next;

        }
        D.faces.add(f);

    }

    public static HalfEdge insertInitial(Subdivision D, Vertex v1, Vertex v2) {

            HalfEdge e1 = new HalfEdge();
            HalfEdge e1Twin = new HalfEdge();

            e1.v = v1;
            e1.twin = e1Twin;
            e1Twin.v = v2;
            e1Twin.twin = e1;

            e1.next = e1Twin;
            e1.prev = e1Twin;
            e1Twin.next = e1;
            e1Twin.prev = e1;
            v1.incidentEdge = e1;
            v2.incidentEdge = e1Twin;
            if(!D.vertices.contains(v2)) {
                D.vertices.add(v2);
                D.vertexMap.put(v2,new ArrayList<>());
            }
        if(!D.vertices.contains(v1)) {
            D.vertices.add(v1);
            D.vertexMap.put(v1,new ArrayList<>());
        }
        D.halfEdges.add(e1);
        D.halfEdges.add(e1Twin);

            return e1;

    }

    public static HalfEdge insertEdgeWithFace(Subdivision D, Face f, Vertex v1, Vertex v2) {
        if (v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return null;
        }

        if(D.adjacentsEdgesTo(v1).size() == 0 && D.adjacentsEdgesTo(v2).size() == 0) {

            return insertInitial(D,v1,v2);
        }

        HalfEdge e = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;
        HalfEdge ePrev = v1.incidentEdge.prev;

        HalfEdge eTwinNext = v1.incidentEdge;
        HalfEdge eNext =eTwin;
        HalfEdge eTwinPrev = e;

        Face fMap = f;
        HalfEdge v1Hf = findFrom(f.outerComponent,v1);
        ePrev = v1Hf.prev;
        eTwinNext = v1Hf;

        e.next = eNext;
        e.prev = ePrev;

        e.next.prev = e;
        e.prev.next = e;

        eTwin.prev = eTwinPrev;
        eTwin.next = eTwinNext;

        eTwin.prev.next = eTwin;
        eTwin.next.prev = eTwin;

        v1.incidentEdge = e;
        v2.incidentEdge = eTwin;
        D.halfEdges.add(e);
        D.halfEdges.add(eTwin);
        if(!D.vertices.contains(v2)) {
            D.vertices.add(v2);
        }

        if(fMap == null) {
            D.vertexMap.put(v2,new ArrayList<>());
            return e;
        }
        var l = D.vertexMap.get(v1);
        var toComplete = D.toComplete.getOrDefault(v1,new ArrayList<>());
        toComplete.add(f);
        D.toComplete.put(v1,toComplete);
        ArrayList<Face> faces = new ArrayList<>();
        faces.add(fMap);
        D.vertexMap.put(v2,faces);
        var toCompleteV2 = D.toComplete.getOrDefault(v2,new ArrayList<>());
        toCompleteV2.add(f);
        D.toComplete.put(v2,toCompleteV2);
        f.outerComponent = e;

        //D.faces.add(fMap.get());
        e.incidentFace = fMap;

        Face fTwin = eTwinNext.incidentFace;
        if(fTwin == null) {
            eTwin.incidentFace = null;
        } else {
            fTwin.outerComponent = eTwin;
            eTwin.incidentFace = fTwin;
        }

        return e;
    }
    public static HalfEdge insertEdgeNoFace(Subdivision D, Vertex v1, Vertex v2) {
        if (v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return null;
        }

        if(D.adjacentsEdgesTo(v1).size() == 0 && D.adjacentsEdgesTo(v2).size() == 0) {

            return insertInitial(D,v1,v2);
        }

        HalfEdge e = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;
        HalfEdge ePrev = v1.incidentEdge.prev;

        HalfEdge eTwinNext = v1.incidentEdge;
        HalfEdge eNext =eTwin;
        HalfEdge eTwinPrev = e;
        var list = D.vertexMap.get(v1).stream().toList();
        Face fMap = null;
        var horaryMostEdge =EdgeUtility.getHoraryMostEdgeFrom(v1);

        boolean inCycle = EdgeUtility.isInCycle(horaryMostEdge);
        if(!inCycle) {
            // TODO a revoir semble inutile
            // SI pas dans un cycle on obtient les faces de v1
            var listMap = D.vertexMap.get(v1);
            //Si pas de face
            if(listMap.isEmpty()) {
                // On récupére aucune face
                fMap = null;
            }else {
                // On récupère la première
                fMap = D.vertexMap.get(v1).get(0);
            }

        } else {
            // On regarde toutes les faces

            for(Face fMpaList : list) {
                // Si le vertex est à l'intérieur on récupère la face
                if(D.isVertexInFace(fMpaList,v2)) {
                    fMap = fMpaList;
                    break;
                }
            }
        }

        if(fMap != null) {
            Face f = fMap;
            HalfEdge v1Hf = findFrom(f.outerComponent,v1);
            ePrev = v1Hf.prev;
            eTwinNext = v1Hf;
        } else {
            // Si pas de face récupére celle des plus dans le sens horaire
            // Selon si dans un cycle
            if(inCycle) {

            }
            ePrev = horaryMostEdge.prev;
            eTwinNext = horaryMostEdge;
        }
        e.next = eNext;
        e.prev = ePrev;

        e.next.prev = e;
        e.prev.next = e;

        eTwin.prev = eTwinPrev;
        eTwin.next = eTwinNext;

        eTwin.prev.next = eTwin;
        eTwin.next.prev = eTwin;

        v1.incidentEdge = e;
        v2.incidentEdge = eTwin;
        D.halfEdges.add(e);
        D.halfEdges.add(eTwin);
        if(!D.vertices.contains(v2)) {
            D.vertices.add(v2);
        }

        if(fMap == null) {
            D.vertexMap.put(v2,new ArrayList<>());
            return e;
        }
        Face f = new Face();
        var l = D.vertexMap.get(v1);
        var toComplete = D.toComplete.getOrDefault(v1,new ArrayList<>());
        toComplete.add(f);
        D.toComplete.put(v1,toComplete);
        ArrayList<Face> faces = new ArrayList<>();
        faces.add(fMap);
        D.vertexMap.put(v2,faces);
        var toCompleteV2 = D.toComplete.getOrDefault(v2,new ArrayList<>());
        toCompleteV2.add(f);
        D.toComplete.put(v2,toCompleteV2);
        f.outerComponent = e;

        //D.faces.add(fMap.get());
        e.incidentFace = fMap;

        Face fTwin = eTwinNext.incidentFace;
        if(fTwin == null) {
            eTwin.incidentFace = null;
        } else {
            fTwin.outerComponent = eTwin;
            eTwin.incidentFace = fTwin;
        }

        return e;
    }

    /**
     * Assume that e and eTwin are on the same face if not then error could spread.
     * @param e
     * @param e2
     */
    public static void connectEdges(HalfEdge e, HalfEdge e2) {

    }

    /**
     * To remove correctly ensure that the next vertex of v2 is v1.
     * @param D
     * @param v1
     * @param v2
     */
    public static void removeEdge(Subdivision D,Vertex v1, Vertex v2) {
        if (v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return;
        }

        HalfEdge e = v1.incidentEdge;
        HalfEdge eTwin = v1.incidentEdge.twin;

        HalfEdge eNext = v1.incidentEdge.prev.twin;
        HalfEdge ePrev = v1.incidentEdge.prev;
        HalfEdge eTwinNext = v2.incidentEdge.prev.twin;
        HalfEdge eTwinPrev = v2.incidentEdge.prev;

        Optional<Face> fMap = D.vertexMap.get(v1).stream().filter(f -> D.vertexMap.get(v2).contains(f)).findFirst();
        if(fMap.isPresent()) {
            Face f = fMap.get();
            //HalfEdge v1Hf = findFrom(f.outerComponent,v1);
            //HalfEdge v2Hf = findFrom(f.outerComponent,v2);
            //v1Hf = D.getEdge(v1,v2);
            //v2Hf = v1Hf.twin;
            var edgeFound = D.getEdge(v1,v2);
            e = edgeFound;
            //eTwin = e.twin;
            eNext = edgeFound.prev.twin;
            ePrev = edgeFound.prev;
            //eNext.v = v1;

            eTwinNext = edgeFound.next;
            eTwinPrev = edgeFound.twin.prev;
            eTwinNext.v = v2;
        } else {
            throw new RuntimeException("No same Faces");
        }
        D.halfEdges.remove(e);
        D.halfEdges.remove(e.twin);
        boolean oneSided = false;
        if(e.next.v.equals(v1) && e.prev.v.equals(v1) ) {
            ePrev.next = eNext;
            eNext.prev = ePrev;


            if(D.adjacentsEdgesTo(v2).isEmpty()) {
                v2.incidentEdge = null;
            } else {
                v2.incidentEdge = eNext;
            }
            v1.incidentEdge = eNext;
            oneSided = true;
        } else if (e.next.v.equals(v2) && e.prev.v.equals(v2)) {

            eTwinNext.prev = eTwinPrev;
            eTwinPrev.next = eTwinNext;



            if(D.adjacentsEdgesTo(v1).isEmpty()) {
                v1.incidentEdge = null;
            } else {
                v1.incidentEdge = eTwinNext;
            }

            v2.incidentEdge = eTwinNext;
            oneSided = true;

        }
        if(eTwin.next.v.equals(v2) && eTwin.prev.v.equals(v2)) {
            eTwinNext.prev = eTwinPrev;
            eTwinPrev.next = eTwinNext;



            if(D.adjacentsEdgesTo(v1).isEmpty()) {
                v2.incidentEdge = null;
            } else {
                v2.incidentEdge = eTwinNext;
            }

            if(!oneSided)
                v1.incidentEdge = eTwinNext;
            oneSided = true;
        } else if(eTwin.next.v.equals(v1) && eTwin.prev.v.equals(v1)) {
            eNext.prev = ePrev;
            ePrev.next = eNext;



            if(D.adjacentsEdgesTo(v2).isEmpty()) {
                v2.incidentEdge = null;
            } else {
                v2.incidentEdge = eTwinNext;
            }
            if(!oneSided)
                v1.incidentEdge = eTwinNext;
            oneSided = true;
        }
        if(oneSided) return;

        ePrev.next = eNext;

        eNext.prev = ePrev;
        //eNext.twin.next = ePrev.twin;
        //ePrev.twin.prev = eNext.twin;





        eTwinPrev.next = eTwinNext;
        eTwinNext.prev = eTwinPrev;

        v1.incidentEdge = eNext;
        v2.incidentEdge = eTwinNext;
        fMap.get().outerComponent = eNext;
    }

    public static HalfEdge insertDiagonal(Subdivision D,Vertex v1, Vertex v2) {
        if (v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return null;
        }

        if(D.adjacentsEdgesTo(v2).size() == 0) {
            return insertEdgeNoFace(D,v1,v2);
        } else if(D.adjacentsEdgesTo(v1).size() == 0) {
            return insertEdgeNoFace(D,v1,v2);
        }

        HalfEdge e = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;
        HalfEdge eNext = v2.incidentEdge;
        HalfEdge ePrev = v1.incidentEdge.prev;
        HalfEdge eTwinNext = v1.incidentEdge;
        HalfEdge eTwinPrev = v2.incidentEdge.prev;

        Optional<Face> fMap = D.vertexMap.get(v1).stream().filter(f -> D.vertexMap.get(v2).contains(f)).findFirst();
        if(fMap.isPresent()) {
            Face f = fMap.get();
            HalfEdge v1Hf = findFrom(f.outerComponent,v1);
            HalfEdge v2Hf = findFrom(f.outerComponent,v2);
            eNext = v2Hf;
            ePrev = v1Hf.prev;
            eTwinNext = v1Hf;
            eTwinPrev = v2Hf.prev;
        } else {
            System.err.println("No same Faces");

        }
        e.next = eNext;
        e.prev = ePrev;

        e.next.prev = e;
        e.prev.next = e;

        eTwin.prev = eTwinPrev;
        eTwin.next = eTwinNext;

        eTwin.prev.next = eTwin;
        eTwin.next.prev = eTwin;

        v1.incidentEdge = e;
        v2.incidentEdge = eTwin;
        D.halfEdges.add(e);
        D.halfEdges.add(eTwin);

        var toComplete = D.toComplete.getOrDefault(v2,new ArrayList<>());
        Face f ;
        ArrayList<Vertex> wasInToComplete = new ArrayList<>();
        if(!toComplete.isEmpty()) {
            f = toComplete.getFirst();
            var list = D.findAllWhere(f);
            wasInToComplete =  list;
            for(Vertex edge : list) {
                var to = D.toComplete.get(edge);
                to.remove(f);
                if(edge != v2) {
                    var mapV = D.vertexMap.get(edge);
                    mapV.add(f);
                }
            }
        }else {
            f = new Face();
        }

        var l = D.vertexMap.get(v1);
        l.add(f);
        var l2 = D.vertexMap.get(v2);
        l2.add(f);
        D.faces.add(f);
        f.outerComponent = e;
        e.incidentFace = f;
        HalfEdge current = e.next;
        while (current != e) {
            if(current.v.equals(v1) || current.v.equals(v2)) {
                current.incidentFace = f;
                current = current.next;
                continue;
            }
            if(!wasInToComplete.contains(current.v))  {
                var l3 = D.vertexMap.get(current.v);

                l3.remove(current.incidentFace);
                l3.add(f);
            }

            current.incidentFace = f;
            current = current.next;
        }
        Face fTwin = eTwinNext.incidentFace;
        if(fTwin != null) {
            fTwin.outerComponent = eTwin;
            eTwin.incidentFace = fTwin;
        }

        return e;


    }

    public static void removeDiagonal(Subdivision D,Vertex v1, Vertex v2) {
        if (v2 == null) {
            System.out.println("V2 est null dans insertDiagonal");
            return;
        }

        HalfEdge e = v1.incidentEdge;
        HalfEdge eTwin = v2.incidentEdge;

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;
        HalfEdge eNext = v2.incidentEdge;
        HalfEdge ePrev = v1.incidentEdge.prev;
        HalfEdge eTwinNext = v1.incidentEdge;
        HalfEdge eTwinPrev = v2.incidentEdge.prev;

        var listMaps = D.vertexMap.get(v1).stream().filter(f -> D.vertexMap.get(v2).contains(f)).toList();
        if(!listMaps.isEmpty() && listMaps.size() >= 2) {
            e = D.getEdge(v1,v2);
            eTwin = e.twin;
            eNext = eTwin.next;
            ePrev = e.prev;
            eTwinNext = e.next;
            eTwinPrev = eTwin.prev;
        } else {
            throw new RuntimeException("No same Faces");
        }

        D.halfEdges.remove(e);
        D.halfEdges.remove(eTwin);
        eNext.prev = ePrev;
        ePrev.next = eNext;
        eTwinNext.prev = eTwinPrev;
        eTwinPrev.next = eTwinNext;

        v1.incidentEdge = eNext;
        v2.incidentEdge = eTwinNext;
        Face toRemove = listMaps.get(0);
        Face toReform = listMaps.get(1);
        var l = D.vertexMap.get(v1);
        l.remove(toRemove);
        var l2 = D.vertexMap.get(v2);
        l2.remove(toRemove);
        eNext.incidentFace = toReform;
        toReform.outerComponent = eNext;
        HalfEdge current = eNext.next;
        while (current != eNext) {
            current.incidentFace = toReform;
            current = current.next;
        }


    }

    public static HalfEdge findFrom(HalfEdge e, Vertex Final) {
        if(e.v.equals(Final)) return e;
        HalfEdge current = e.next;

        do  {
            if(current.v.equals(Final)) {
                return  current;
            }
            current = current.next;
        } while (current != e);
        return null;
    }
    public static HalfEdge findFromWhereTwin(HalfEdge e, Vertex Final,Vertex twin) {
        if(e.v.equals(Final) && e.twin.v.equals(twin)) return e;
        if(e.twin.v.equals(Final) && e.v.equals(twin)) {
            return e.twin;
        }
        HalfEdge current = e.next;

        do  {
            if(current.v.equals(Final) && e.twin.v.equals(twin)) {
                return  current;
            }
            if(current.twin.v.equals(Final) && current.v.equals(twin)) {
                return current.twin;
            }
            current = current.next;
        } while (current != e);
        return null;
    }

    public static getDiagonal diagonal(Subdivision D, Vertex v1, Vertex v2) {

        HalfEdge e = new HalfEdge();
        HalfEdge eTwin = new HalfEdge();

        e.v = v1;         // pointe vers l'autre sommet
        e.twin = eTwin;

        eTwin.v = v2;
        eTwin.twin = e;
        HalfEdge eNext = v2.incidentEdge;
        HalfEdge ePrev = v1.incidentEdge.prev;
        HalfEdge eTwinNext = v1.incidentEdge;
        HalfEdge eTwinPrev = v2.incidentEdge.prev;

        Optional<Face> fMap = D.vertexMap.get(v1).stream().filter(f -> D.vertexMap.get(v2).contains(f)).findFirst();
        if(fMap.isPresent()) {
            Face f = fMap.get();
            HalfEdge v1Hf = findFrom(f.outerComponent,v1);
            HalfEdge v2Hf = findFrom(f.outerComponent,v2);
            eNext = v2Hf;
            ePrev = v1Hf.prev;
            eTwinNext = v1Hf;
            eTwinPrev = v2Hf.prev;
        } else {
            return null;
        }
        e.next = eNext;
        e.prev = ePrev;

        eTwin.prev = eTwinPrev;
        eTwin.next = eTwinNext;

        Face f = new Face();
        f.outerComponent = e;


        getDiagonal result = new getDiagonal(e, eTwin,f);
        return result;
    }

    private record getDiagonal(HalfEdge e, HalfEdge eTwin,Face newFace) {
    }
    public static boolean insideP(Subdivision D,Vertex v, Vertex next) {
        double angle = computeAngleTo(v);
        double angleNext = computeAngleTo(next);

        HalfEdge between = diagonal(D,v,next).e;
        double angleDiagonal = computeAngleTo(between);
        return angleDiagonal-angle < 0 ;

    }

    public static double computeAngleTo(Vertex v) {
        double baseX = v.x;
        double baseY = v.y;
        Vertex nextV = v.incidentEdge.next.v;
        Vertex prevV = v.incidentEdge.prev.v;

        double nextX = nextV.x -baseX;
        double nextY = nextV.y - baseY;
        double prevX = prevV.x - baseX;
        double prevY = prevV.y - baseY;

        double angle = computeAngle(List.of(nextX,nextY),List.of(prevX,prevY));
        return angle ;
    }

    public static double computeAngleTo(HalfEdge edge) {
        Vertex v = edge.v;

        double baseX = v.x;
        double baseY = v.y;
        Vertex nextV = edge.next.v;
        Vertex prevV = edge.prev.v;

        double nextX = nextV.x -baseX;
        double nextY = nextV.y - baseY;
        double prevX = prevV.x - baseX;
        double prevY = prevV.y - baseY;

        double angle = computeAngle(List.of(nextX,nextY),List.of(prevX,prevY));
        return angle ;
    }

    public static double crossProduct(List<Double> a, List<Double> b) {
        return a.get(0) * b.get(1) - a.get(1) * b.get(0);
    }

    public static double dotProduct(List<Double> a, List<Double> b) {
        return a.get(0) * b.get(0) + a.get(1) * b.get(1);
    }
    public static double computeAngle(List<Double> a, List<Double> b)  {
        double angle = Math.atan2(crossProduct(a,b),dotProduct(a,b));
        if (angle < 0) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    public double topY() { return Math.max(getY(), twin.getY()); }

    // Retourne le y du sommet inférieur
    public double bottomY() { return Math.min(getY(), twin.getY()); }

    // Vérifie si l'arête intersecte la sweep line en y
    public boolean intersects(double sweepY) {
        return sweepY <= topY() && sweepY >= bottomY();
    }
}
