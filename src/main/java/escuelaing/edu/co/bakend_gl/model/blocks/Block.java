package escuelaing.edu.co.bakend_gl.model.blocks;

public abstract class Block {
    protected int x;
    protected int y;
    protected String type;
    protected boolean destructible;
    protected String allowedCharacter; // Qué personaje puede destruirlo

    public Block(int x, int y, String type, boolean destructible, String allowedCharacter) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.destructible = destructible;
        this.allowedCharacter = allowedCharacter;
    }

    public String getType() { return type; }
    public boolean isDestructible() { return destructible; }
    public String getAllowedCharacter() { return allowedCharacter; }
    public int getX() { return x; }
    public int getY() { return y; }
}