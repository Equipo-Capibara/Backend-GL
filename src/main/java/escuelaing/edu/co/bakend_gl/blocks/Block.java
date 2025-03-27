package escuelaing.edu.co.bakend_gl.blocks;

public abstract class Block {
    protected String type;
    protected boolean destructible;
    protected String allowedCharacter;

    public Block(String type, boolean destructible, String allowedPlayer){
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
}
