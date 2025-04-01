package escuelaing.edu.co.bakend_gl.model.characters;

public abstract class Character {
    protected int x, y;
    protected String element;

    public Character(int x, int y, String element) {
        this.x = x;
        this.y = y;
        this.element = element;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getElement() { return element; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void useAbility();
}