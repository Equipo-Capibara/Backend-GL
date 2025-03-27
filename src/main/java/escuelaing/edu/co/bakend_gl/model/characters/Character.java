package escuelaing.edu.co.bakend_gl.model.characters;

public class Character {
    private int x;
    private int y;

    public Character(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
