package entities;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

public class HalfEdge {

    public int id;
    public HalfVertex vertex;
    public HalfEdge pair;
    public HalfFace face;
    public HalfEdge next;
    public Vector2f texture;
    public Vector2f old_texture;
    public boolean crease;

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
        this.crease = false;
    }

    public HalfEdge(){
        this.texture = new Vector2f();
        this.old_texture = new Vector2f();
        this.pair = null;
        this.crease = false;
    }

    public void setOld_texture() {
        this.old_texture.x = this.texture.x;
        this.old_texture.y = this.texture.y;
    }

    public HalfEdge previous() {
        HalfEdge prev = next;
        while(prev.next != this)
            prev = prev.next;
        return prev;
    }

    public HalfEdge previous2() {
        HalfEdge prev = next;
        while(prev.next.next != this)
            prev = prev.next;
        return prev;
    }
    public HalfEdge rewind()
    {
        if(pair == null)
            return this;
        HalfEdge e = pair.previous();
        while(e != this && e.pair != null)
            e = e.pair.previous();
        return e;
    }

    public HalfEdge toNextCrease() {
        HalfEdge eloop = this;
        int status = 0;
        do {
            if(eloop == null)
            {
                status = 1;
                break;
            }
            if(eloop.crease)
            {
                status = 2;
                return eloop;
            }
            eloop = eloop.next.pair;
        }while(eloop != this);
        if(status == 0)
        {
            System.out.println("SHOULD NOT BE HERE");
            return null;
        }
        else if(status == 1)
        {
            eloop = this;
            do {
                if(eloop == null)
                    return null;
                if(eloop.crease)
                    return eloop;
                eloop = eloop.pair.previous();
            }while (eloop != this);
        }
        return null;
    }

    public HalfEdge toNextCrease2() {
        HalfEdge eloop = this.next.pair;
        int status = 0;
        do {
            if(eloop == null)
            {
                status = 1;
                break;
            }
            if(eloop.crease)
            {
                status = 2;
                return eloop;
            }
            eloop = eloop.next.pair;
        }while(eloop != this);
        if(status == 0)
        {
            System.out.println("SHOULD NOT BE HERE");
            return null;
        }
        else if(status == 1)
        {
            eloop = this.pair.previous();
            do {
                if(eloop == null)
                    return null;
                if(eloop.crease)
                    return eloop;
                eloop = eloop.pair.previous();
            }while (eloop != this);
        }
        return null;
    }
}
