package escuelaing.edu.co.bakend_gl.model.characters;

public abstract class Character {
    protected int x, y;
    protected String element;

    protected String directionView; // s - abajo, w - arriba, a - izq, d - der

    public Character(int x, int y, String element) {
        this.x = x;
        this.y = y;
        this.element = element;
        this.directionView = "s";
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getElement() { return element; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void useAbility();

    public void setDirectionView(String directionView){this.directionView = directionView;}

    public String getDirectionView(){return this.directionView;}


}