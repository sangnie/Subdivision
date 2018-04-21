package entities;

import models.RawModel;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import javafx.util.Pair;
import renderEngine.Loader;

import javax.swing.*;

public class Mesh {

    public ArrayList<HalfEdge> edges;
    public ArrayList<HalfVertex> vertices;
    public ArrayList<HalfFace> faces;


    public Mesh() {
        edges = new ArrayList<>();
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
    }

    public void loadFromFile(String fileName)
    {
        FileReader fr = null;

        try {
            fr = new FileReader(new File("res/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load file!");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String line;
//        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Integer> indices = new ArrayList<Integer>();


        Map<Pair<Integer, Integer>, HalfEdge> halfedges = new HashMap<>();
        try {

            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(new HalfVertex(vertices.size(),vertex,null));
                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
//                    textureArray = new float[vertices.size() * 2];
//                    normalsArray = new float[vertices.size() * 3];
                    break;
                }
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                int vid1 = Integer.parseInt(vertex1[0]) - 1;
                int vid2 = Integer.parseInt(vertex2[0]) - 1;
                int vid3 = Integer.parseInt(vertex3[0]) - 1;
                HalfVertex v1 = vertices.get(vid1);
                HalfVertex v2 = vertices.get(vid2);
                HalfVertex v3 = vertices.get(vid3);

                int lastEdge = edges.size();

                HalfEdge e3 =new HalfEdge(lastEdge,v1,null,null,null,textures.get(Integer.parseInt(vertex1[1])-1));
                Vector3f currentNorm = normals.get(Integer.parseInt(vertex1[2])-1);
                HalfFace f = new HalfFace(faces.size(),currentNorm,e3);
                faces.add(f);
                HalfEdge e2 = new HalfEdge(lastEdge + 1, v3, null, f, e3,textures.get(Integer.parseInt(vertex3[1])-1));
                HalfEdge e1 = new HalfEdge(lastEdge + 2, v2, null, f, e2,textures.get(Integer.parseInt(vertex2[1])-1));
                e3.face = f;
                e3.next = e1;

                edges.add(e3);
                edges.add(e2);
                edges.add(e1);

//                v1.edge = e1;
//                v2.edge = e2;
//                v3.edge = e3;

                v1.edge = e3;
                v2.edge = e1;
                v3.edge = e2;

                halfedges.put(new Pair<>(vid1,vid2),e1);
                halfedges.put(new Pair<>(vid2,vid3),e2);
                halfedges.put(new Pair<>(vid3,vid1),e3);

                if(halfedges.containsKey(new Pair<>(vid2,vid1))){
                    HalfEdge e1_ = halfedges.get(new Pair<>(vid2,vid1));
                    e1.pair = e1_;
                    e1_.pair = e1;
                }
                if(halfedges.containsKey(new Pair<>(vid3,vid2))){
                    HalfEdge e2_ = halfedges.get(new Pair<>(vid3,vid2));
                    e2.pair = e2_;
                    e2_.pair = e2;
                }
                if(halfedges.containsKey(new Pair<>(vid1,vid3))){
                    HalfEdge e3_ = halfedges.get(new Pair<>(vid1,vid3));
                    e3.pair = e3_;
                    e3_.pair = e3;
                }

//                processVertex(vertex1,indices,textures,normals,textureArray,normalsArray);
//                processVertex(vertex2,indices,textures,normals,textureArray,normalsArray);
//                processVertex(vertex3,indices,textures,normals,textureArray,normalsArray);
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Faces: " + faces.size() + ", Vertices: " + vertices.size() + ", Edges: " + edges.size());

//        verticesArray = new float[vertices.size()*3];
//        indicesArray = new int[indices.size()];
//
//        int vertexPointer = 0;
//        for(Vector3f vertex:vertices){
//            verticesArray[vertexPointer++] = vertex.x;
//            verticesArray[vertexPointer++] = vertex.y;
//            verticesArray[vertexPointer++] = vertex.z;
//        }
//
//        for(int i=0;i<indices.size();i++){
//            indicesArray[i] = indices.get(i);
//        }
    }

    public RawModel loadObjModel(Loader loader) {

        int numFaces = faces.size();
        calculateNormals();

        float[] verticesArray = new float[numFaces*3*3];
        float[] normalsArray = new float[numFaces*3*3];
        float[] textureArray = new float[numFaces*2*3];
        int[] indicesArray = new int[numFaces*3];

        int nf = 0;
        int tf = 0;
        for(HalfFace face:faces){
//            HalfVertex v1 = face.edge.vertex;
//            HalfVertex v2 = v1.edge.vertex;
//            HalfVertex v3 = v2.edge.vertex;
//
//            verticesArray[nf] = v1.posn.x;
//            verticesArray[nf+1] = v1.posn.x;
//            verticesArray[nf+2] = v1.posn.x;

//            for (int i = 0; i < 9; i++) {
//                normalsArray[nf + i] = face.normal.x;
//            }

            HalfEdge edge = face.edge;
            do {
                // do something with edge
                HalfVertex v = edge.vertex;
                verticesArray[nf] = v.posn.x;
                verticesArray[nf+1] = v.posn.y;
                verticesArray[nf+2] = v.posn.z;
                normalsArray[nf] = face.normal.x;
                normalsArray[nf+1] = face.normal.y;
                normalsArray[nf+2] = face.normal.z;
                textureArray[tf] = edge.texture.x;
                textureArray[tf+1] = edge.texture.y;
                indicesArray[nf/3] = nf/3;
                nf +=3;
                tf +=2;
                edge = edge.next;
            } while (edge != face.edge);
        }

        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }

    public void calculateNormals(){
        for(HalfFace face:faces){
            HalfEdge e = face.edge;
            Vector3f e1 = new Vector3f();
            System.out.println(e.id);
            System.out.println(e.vertex.id);
            System.out.println(e.vertex.posn);
            System.out.println(e.next.id);
            System.out.println(e.next.vertex.id);
            System.out.println(e.next.vertex.posn);
            Vector3f.sub(e.vertex.posn, e.next.vertex.posn,e1);
            System.out.println("YAY");
            e = e.next;
            Vector3f e2 = new Vector3f();
            Vector3f.sub(e.vertex.posn, e.next.vertex.posn,e2);
            face.normal = Vector3f.cross(e1,e2,face.normal);
//            System.out.println(face.normal);
            face.normal.normalise();
//            System.out.println(e1);
//            System.out.println(e2);
//            System.out.println(face.normal);
        }
    }

    public static void subdivide(Mesh m){

        int evenverts = m.vertices.size();
        System.out.println("@@@@@@@@@@@@@@@@@");

        subdivideEdges(m);
        System.out.println("@@@@@@@@@@@@@@@@@");

        updateOriginal(m,evenverts);
        System.out.println("@@@@@@@@@@@@@@@@@");

        splitFaces(m);

        System.out.println("@@@@@@@@@@@@@@@@@");
    }

//    public void subdivide(int number){
//        for (int i = 0; i < number; i++) {
//            subdivide();
//        }
//    }

    public static void splitFaces(Mesh m){

        ArrayList<HalfFace> new_faces = new ArrayList<>();

        for (HalfFace face:m.faces){
            HalfEdge e0 = face.edge;
            HalfEdge e_prev = e0;
            while(e_prev.next != e0){
                e_prev = e_prev.next;
            }

            HalfEdge outer1=null, outer2=null, outer3=null;

            do{
                HalfEdge e = e0.next;
                HalfEdge e0_next = e.next;

                HalfFace f = new HalfFace(new_faces.size(),null, e0);
                new_faces.add(f);
                e0.face = f;
                e.face = f;

                HalfEdge new_e = new HalfEdge(m.edges.size(),e_prev.vertex,null,f,e0,e_prev.texture);
                m.edges.add(new_e);
                e.next = new_e;

                if (outer1==null) outer1 = new_e;
                else if(outer2==null) outer2 = new_e;
                else outer3 = new_e;

                e_prev = e;
                e0 = e0_next;
            } while (e0 != face.edge);

            //CREATE INNER FACE
            HalfFace f = new HalfFace(new_faces.size(),null,null);
            new_faces.add(f);
//            HalfEdge inner1 = new HalfEdge();
//            HalfEdge inner2 = new HalfEdge();
//            HalfEdge inner3 = new HalfEdge();
            int eSize = m.edges.size();
            HalfEdge inner3 = new HalfEdge(eSize+2,outer1.vertex,outer3,f,null,outer1.texture);
            HalfEdge inner2 = new HalfEdge(eSize+1,outer3.vertex,outer2,f,inner3,outer3.texture);
            HalfEdge inner1 = new HalfEdge(eSize,outer2.vertex,outer1,f,inner2,outer2.texture);
            inner3.next = inner1;
            m.edges.add(inner1);
            m.edges.add(inner2);
            m.edges.add(inner3);
            f.edge = inner3;
        }
        m.faces = new_faces;
    }

    public static void updateOriginal(Mesh m, int evenverts)
    {
        for(int i = 0 ; i < evenverts ; i++)
        {
            HalfVertex v = m.vertices.get(i);
            ArrayList<Vector3f> neighbours = new ArrayList<Vector3f>();
            ArrayList<Vector2f> textures = new ArrayList<Vector2f>();
            HalfEdge e0 = v.edge;
            HalfEdge e = e0;
            do {
                textures.add(e.pair.next.texture);
                neighbours.add(e.pair.next.vertex.posn);
                e = e.next.pair;
            }while (e != e0);
            int n = neighbours.size();
            float beta;
            if(n == 3)
                beta = 3.0f / 16.0f;
            else
                beta = 3.0f / (8.0f * n);
            v.posn.x = (1.0f - n*beta)*v.posn.x;
            v.posn.y = (1.0f - n*beta)*v.posn.y;
            v.posn.z = (1.0f - n*beta)*v.posn.z;

            v.edge.texture.x = (1.0f - n*beta)*v.edge.texture.x;
            v.edge.texture.y = (1.0f - n*beta)*v.edge.texture.y;

            for(int j = 0 ; j < neighbours.size() ; j++)
            {
                Vector3f temp = neighbours.get(j);
                temp.x = beta*temp.x;
                temp.y = beta*temp.y;
                temp.z = beta*temp.z;
                Vector3f.add(v.posn,temp,v.posn);

                Vector2f temp2 = textures.get(j);
                temp2.x = beta*temp2.x;
                temp2.y = beta*temp2.y;
                Vector2f.add(v.edge.texture,temp2,v.edge.texture);
            }
        }
    }

    public static void subdivideEdges(Mesh m)
    {
        ArrayList<Integer> split_edges = new ArrayList<Integer>();
//        split_edges.add(-1);
        int evenedges = m.edges.size();
        int evenvertices = m.vertices.size();
        for(int i = 0 ; i < evenedges ; i++)
        {
            HalfEdge e_split = m.edges.get(i);
            HalfEdge e_prev = e_split;
            while(e_prev.next != e_split)
                e_prev = e_prev.next;

            HalfVertex v_start = e_prev.vertex;
            HalfVertex v_end = e_split.vertex;

            HalfEdge e = new HalfEdge();
            e.id = m.edges.size();
            e.face = e_split.face;

            e.next = e_split;
            e_prev.next = e;

            if(e_split.pair == null || !split_edges.contains(e_split.pair.id))
            {
                HalfVertex midpoint = new HalfVertex();
                midpoint.id = m.vertices.size();
                midpoint.edge = e;

                if(e_split.pair != null)
                {
//                    HalfVertex opp1 = edges.get(e_split.id).next.vertex;
//                    HalfVertex opp2 = edges.get(e_split.id).pair.next.vertex;
                    HalfVertex opp1,opp2;
                    if(e_split.next.vertex.id >= evenvertices) {
                        opp1 = e_split.next.next.vertex;
                    }
                    else {
                        opp1 = e_split.next.vertex;
                    }
                    if(e_split.pair.next.vertex.id >= evenvertices) {
                        opp2 = e_split.pair.next.next.vertex;
                    }
                    else {
                        opp2 = e_split.pair.next.vertex;
                    }
                    Vector3f pos1 = new Vector3f();
                    Vector3f.add(v_start.posn,v_end.posn,pos1);
                    pos1.x = 3.0f*pos1.x/8.0f;
                    pos1.y = 3.0f*pos1.y/8.0f;
                    pos1.z = 3.0f*pos1.z/8.0f;
                    Vector3f pos2 = new Vector3f();
                    Vector3f.add(opp1.posn,opp2.posn,pos2);
                    pos2.x = 1.0f*pos2.x/8.0f;
                    pos2.y = 1.0f*pos2.y/8.0f;
                    pos2.z = 1.0f*pos2.z/8.0f;
                    Vector3f.add(pos1,pos2,midpoint.posn);

                    Vector2f tex1 = new Vector2f();
                    Vector2f.add(v_start.edge.texture,v_end.edge.texture,tex1);
                    tex1.x = 3.0f*tex1.x/8.0f;
                    tex1.y = 3.0f*tex1.y/8.0f;
                    Vector2f tex2 = new Vector2f();
                    Vector2f.add(opp1.edge.texture,opp2.edge.texture,tex2);
                    tex2.x = 1.0f*tex2.x/8.0f;
                    tex2.y = 1.0f*tex2.y/8.0f;
                    Vector2f.add(tex1,tex2,midpoint.edge.texture);

                }
                else
                {
                    Vector3f.add(v_start.posn,v_end.posn,midpoint.posn);
                    midpoint.posn.x *= 0.5f;
                    midpoint.posn.y *= 0.5f;
                    midpoint.posn.z *= 0.5f;

                    Vector2f.add(v_start.edge.texture,v_end.edge.texture,midpoint.edge.texture);
                    midpoint.edge.texture.x *= 0.5f;
                    midpoint.edge.texture.y *= 0.5f;
                }

                e.vertex = midpoint;
                System.out.println("###" + midpoint.posn);
                m.vertices.add(midpoint);
            }
            else
            {
                HalfEdge old_pair = m.edges.get(e_split.pair.id);

                HalfEdge pair_prev = old_pair;
                while(pair_prev.next != old_pair)
                    pair_prev = pair_prev.next;

                e_split.pair = pair_prev;
                pair_prev.pair = e_split;

                e.vertex = pair_prev.vertex;
                e.texture = pair_prev.texture;

                old_pair.pair = e;
                e.pair = old_pair;

            }

            m.edges.add(e);
            split_edges.add(e_split.id);
        }
    }

}
