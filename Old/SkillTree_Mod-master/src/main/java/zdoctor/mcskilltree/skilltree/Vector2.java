package zdoctor.mcskilltree.skilltree;

import java.util.Vector;

public class Vector2 extends Vector<Integer> {

    public static final Vector2 ZERO = new Vector2();

    public Vector2() {
        this(0, 0);
    }

    public Vector2(int x, int y) {
        super(2);
        add(x);
        add(y);
    }

    public Vector2(Vector2 from) {
        super(from);
    }

    public int getX() {
        return elementAt(0);
    }

    public int getY() {
        return elementAt(1);
    }

    public void setX(int x) {
        set(0, x);
    }

    public void setY(int y) {
        set(1, y);
    }

}
