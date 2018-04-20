package entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class Mesh {

    public ArrayList<HalfEdge> edges;
    public ArrayList<HalfVertex> vertices;
    public ArrayList<HalfFace> faces;

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
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        Map<Pair<Integer, Integer>, HalfEdge> halfedges = new Map<Pair<Integer, Integer>, HalfEdge>();
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
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
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

                HalfVertex v1 = vertices.get(Integer.parseInt(vertex1[0]) - 1);
                HalfVertex v2 = vertices.get(Integer.parseInt(vertex2[0]) - 1);
                HalfVertex v3 = vertices.get(Integer.parseInt(vertex3[0]) - 1);

                int lastEdge = edges.size();
                HalfEdge e3 = new HalfEdge(lastEdge, v1, null, null,null);
                Vector3f currentNorm = normals.get(Integer.parseInt(vertex1[2])-1);
                HalfFace f = new HalfFace(faces.size(),currentNorm,e3);
                faces.add(f);
                HalfEdge e2 = new HalfEdge(lastEdge + 1, v3, null, f, e3);
                HalfEdge e1 = new HalfEdge(lastEdge + 2, v2, null, f, e2);
                e3.face = f;
                e3.next = e1;

                edges.add(e3);
                edges.add(e2);
                edges.add(e1);

                v1.edge = e1;
                v2.edge = e2;
                v3.edge = e3;

//                processVertex(vertex1,indices,textures,normals,textureArray,normalsArray);
//                processVertex(vertex2,indices,textures,normals,textureArray,normalsArray);
//                processVertex(vertex3,indices,textures,normals,textureArray,normalsArray);
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private static void processVertex(String[] vertexData, List<Integer> indices,
                                      List<Vector2f> textures, List<Vector3f> normals, float[] textureArray,
                                      float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1])-1);
        textureArray[currentVertexPointer*2] = currentTex.x;
        textureArray[currentVertexPointer*2+1] = 1 - currentTex.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
        normalsArray[currentVertexPointer*3] = currentNorm.x;
        normalsArray[currentVertexPointer*3+1] = currentNorm.y;
        normalsArray[currentVertexPointer*3+2] = currentNorm.z;


    }


}
