package entities;

import models.RawModel;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;
import renderEngine.Loader;

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

                v1.edge = e1;
                v2.edge = e2;
                v3.edge = e3;

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

    public void subdivide(){

    }

    public void subdivide(int number){
        for (int i = 0; i < number; i++) {
            subdivide();
        }
    }

    public void splitFaces(Mesh m){

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
                HalfEdge e0_next = e0.next;

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
                e0 = e0.next;
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
    }
}
