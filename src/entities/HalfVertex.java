package entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class HalfVertex {

    public int id;
    public Vector3f posn;
//    public Vector3f normal;
//    public Vector2f texture;
    public HalfEdge edge;

    public HalfVertex(int size, Vector3f vertex, HalfEdge e) {
        this.id = size;
        this.posn = vertex;
        this.edge = e;
    }

    public HalfVertex(){}
}
