package escuelaing.edu.co.bakend_gl.model.board;

import java.io.Serializable;

public class Door implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int x, y;
    private boolean isLocked;

    // Constructor sin argumentos para deserialización
    public Door() {
    }

    public Door(int x, int y, boolean isLocked) {
        this.x = x;
        this.y = y;
        this.isLocked = isLocked;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void unlock() {
        this.isLocked = false;
    }
}
