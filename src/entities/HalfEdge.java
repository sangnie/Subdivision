package entities;

import org.lwjgl.util.vector.Vector2f;

public class HalfEdge {

    public int id;
    public HalfVertex vertex;
    public HalfEdge pair;
    public HalfFace face;
    public HalfEdge next;
    public Vector2f texture;
    public Vector2f old_texture;

    public HalfEdge(int id, HalfVertex vertex, HalfEdge pair, HalfFace face, HalfEdge next, Vector2f texture) {
        this.id = id;
        this.vertex = vertex;
        this.pair = pair;
        this.face = face;
        this.next = next;
        this.texture = texture;
        old_texture = new Vector2f();
        old_texture.x = texture.x;
        old_texture.y = texture.y;
    }

    public HalfEdge(){
        texture = new Vector2f();
        old_texture = new Vector2f();
    }

    public void setOld_texture() {
        this.old_texture.x = this.texture.x;
        this.old_texture.y = this.texture.y;
    }
}
