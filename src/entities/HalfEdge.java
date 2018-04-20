package entities;

public class HalfEdge {

    public int id;
    public HalfVertex vertex;
    public HalfEdge pair;
    public HalfFace face;
    public HalfEdge next;

    public HalfEdge(int id, HalfVertex vertex, HalfEdge pair, HalfFace face, HalfEdge next) {
        this.id = id;
        this.vertex = vertex;
        this.pair = pair;
        this.face = face;
        this.next = next;
    }
}
