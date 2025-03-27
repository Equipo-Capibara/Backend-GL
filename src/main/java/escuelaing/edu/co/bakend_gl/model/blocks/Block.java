package escuelaing.edu.co.bakend_gl.model.blocks;

public abstract class Block {
    protected int X;
    protected int Y;
    protected String type;
    protected boolean destructible;
    protected String allowedCharacter;

    public Block(int x, int y, String type, boolean destructible, String allowedPlayer){
        this.X = x;
        this.Y = y;
        this.type = type;
        this.destructible = destructible;
        this.allowedCharacter = allowedPlayer;
    }

    public String getType(){
        return this.type;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public String getAllowedCharacter() {
        return allowedCharacter;
    }
    public int getX() {
         return X;
    }
    public int getY() {
        return Y;
    }
}
