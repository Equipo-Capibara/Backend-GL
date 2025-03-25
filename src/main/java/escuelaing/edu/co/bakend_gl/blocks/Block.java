package escuelaing.edu.co.bakend_gl.blocks;

public abstract class Block {
    protected String type;
    protected boolean destructible;
    protected String allowedPlayer;

    public Block(String type, boolean destructible, String allowedPlayer){
        this.type = type;
        this.destructible = destructible;
        this.allowedPlayer = allowedPlayer;
    }

    public String getType(){
        return this.type;
    }

    public boolean isDestructible() {
        return destructible;
    }

    public String getAllowedPlayer() {
        return allowedPlayer;
    }
}
