package entities;

import org.lwjgl.util.vector.Vector3f;

public class HalfFace {

    public int id;
    public Vector3f normal;
    public HalfEdge edge;

    public HalfFace(int id, Vector3f normal, HalfEdge edge) {
        this.id = id;
        this.normal = normal;
        this.edge = edge;
    }

    public HalfFace(){
        normal = new Vector3f();
    }
}
