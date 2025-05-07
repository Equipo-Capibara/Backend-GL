package escuelaing.edu.co.bakend_gl.model.board;

import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.keys.Key;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private int width;
    private int height;
    private Box[][] grid;
    private List<Key> collectedKeys;
    private List<Character> characters;
    private Door door;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.collectedKeys = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.grid = new Box[width][height];
        this.door = null;

        // Inicializar el tablero con casillas vacías
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Box(i, j);
            }
        }
    }

    public void addCharacter(Character character) {
        int x = character.getX();
        int y = character.getY();
        Box box = getBox(x, y);
        if (box != null && box.getCharacter() == null && box.isWalkable()) {
            box.setCharacter(character);
            character.setPosition(x, y);
            characters.add(character);
        } else {
            throw new IllegalStateException("No se puede colocar el personaje en (" + x + "," + y + "). Ya hay algo ahí.");
        }
    }

    public Box getBox(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[x][y];
        }
        return null;
    }

    public boolean isMoveValid(int x, int y) {
        Box box = getBox(x, y);
        return box != null && box.isWalkable();
    }

    public void movePlayer(Character player, int newX, int newY) {
        if (isMoveValid(newX, newY)) {
            Box currentBox = getBox(player.getX(), player.getY());
            if (currentBox != null) {
                currentBox.removeCharacter();
            }

            Box newBox = getBox(newX, newY);
            if (newBox != null) {
                newBox.setCharacter(player);
                player.setPosition(newX, newY);
                collectKey(newBox, player);
            }
        }
    }

    private void collectKey(Box box, Character player) {
        if (box.hasKey() && box.getKey().canBePickedBy(player)) {
            Key key = box.getKey();
            this.collectedKeys.add(key);
            log.info("Llave recogida: {} por jugador: {}", key.getClass().getSimpleName(), player.getPlayerId());

            box.removeKey();

            if (collectedKeys.size() == 4) {
                unlockDoor();
            }
        }
    }

    public void placeDoor(int x, int y) {
        this.door = new Door(x, y, true);
        Box box = getBox(x, y);
        if (box != null) {
            box.setDoor(this.door);
        }
    }

    private void unlockDoor() {
        if (door != null) {
            door.unlock();
            log.info("¡Todas las llaves han sido recogidas! La puerta se ha abierto.");
        }
    }

    public void addBlock(Block block) {
        Box box = getBox(block.getX(), block.getY());
        if (box != null && box.isWalkable()) {
            box.setBlock(block);
        }
    }

    public void removeBlock(int x, int y) {
        Box box = getBox(x, y);
        if (box != null && box.getBlock() != null && box.getBlock().isDestructible()) {
            box.removeBlock();
        }
    }

}
