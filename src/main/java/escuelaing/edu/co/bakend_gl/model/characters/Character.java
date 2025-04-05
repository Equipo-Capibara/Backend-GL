package escuelaing.edu.co.bakend_gl.model.characters;

public abstract class Character {
    protected int x, y;
    protected String element;

    protected String direction = "s"; // w - arriba, s - abajo, a - izq, d - der

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

    public void setDirection(String direction) { this.direction = direction;}

    public String getDirection() { return this.direction;}

    public abstract void useAbility();
}