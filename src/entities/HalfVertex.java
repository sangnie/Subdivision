package entities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class HalfVertex {

    public int id;
    public Vector3f posn;
    public Vector3f old_posn;
//    public Vector3f normal;
//    public Vector2f texture;
    public HalfEdge edge;

    public HalfVertex(int size, Vector3f vertex, HalfEdge e) {
        this.id = size;
        this.posn = vertex;
        this.edge = e;
        old_posn = new Vector3f();
        old_posn.x = vertex.x;
        old_posn.y = vertex.y;
        old_posn.z = vertex.z;
    }

    public HalfVertex()
    {
        this.posn = new Vector3f();
        this.old_posn = new Vector3f();
    }

    public void setOld_posn() {
        this.old_posn.x = this.posn.x;
        this.old_posn.y = this.posn.y;
        this.old_posn.z = this.posn.z;
    }

    public boolean onBoundary() {
        HalfEdge eloop = edge;
        do {
            eloop = eloop.next.pair;
        }while(eloop != edge && eloop != null);
        return  eloop == null;
    }
}
