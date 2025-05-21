package escuelaing.edu.co.bakend_gl.model.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.keys.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Box implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private Block block;
    private Character character;
    private Key key;
    private Door door;
    private boolean walkable;

    public Box(int x, int y) {
        this.x = x;
        this.y = y;
        this.block = null;
        this.character = null;
        this.key = null;
        this.door = null;
        this.walkable = true;
    }

    @JsonIgnore
    public boolean isWalkable() {
        boolean isWalkable = block == null && (door == null || !door.isLocked()) && character == null;
        this.walkable = isWalkable;
        return isWalkable;
    }

    public void setBlock(Block block) {
        this.block = block;
        this.walkable = (block == null && (door == null || !door.isLocked()) && character == null);
    }

    public void removeBlock() {
        this.block = null;
        this.walkable = ((door == null || !door.isLocked()) && character == null);
    }

    public void setCharacter(Character character) {
        this.character = character;
        this.walkable = (block == null && (door == null || !door.isLocked()) && character == null);
    }

    public void removeCharacter() {
        this.character = null;
        this.walkable = (block == null && (door == null || !door.isLocked()));
    }

    public boolean hasKey() {
        return key != null;
    }

    public void removeKey() {
        this.key = null;
    }

    public void setDoor(Door door) {
        this.door = door;
        this.walkable = (block == null && (door == null || !door.isLocked()) && character == null);
    }

}
