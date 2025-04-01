package escuelaing.edu.co.bakend_gl.model.board;

import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.keys.Key;

public class Box {
    private int x, y;
    private Block block;
    private Character character;
    private Key key;
    private Door door;

    public Box(int x, int y) {
        this.x = x;
        this.y = y;
        this.block = null;
        this.character = null;
        this.key = null;
        this.door = null;
    }

    public boolean isWalkable() {
        return block == null && (door == null || !door.isLocked());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Character getCharacter() {
        return character;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void removeBlock() {
        this.block = null;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public void removeCharacter() {
        this.character = null;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public boolean hasKey() {
        return key != null;
    }

    public void removeKey() {
        this.key = null;
    }

    public void setDoor(Door door) {
        this.door = door;
    }

    public Door getDoor() {
        return door;
    }
}
