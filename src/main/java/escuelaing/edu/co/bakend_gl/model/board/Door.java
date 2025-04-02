package escuelaing.edu.co.bakend_gl.model.board;

public class Door {
    private int x, y;
    private boolean isLocked;

    public Door(int x, int y, boolean isLocked) {
        this.x = x;
        this.y = y;
        this.isLocked = isLocked;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void unlock() {
        this.isLocked = false;
    }
}
