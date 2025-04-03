package escuelaing.edu.co.bakend_gl.model.board;

import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.keys.Key;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int width;
    private int height;
    private Box[][] grid;
    private List<Key> collectedKeys;
    private Character player;

    private Character player2;
    private Door door;

    public Board(int width, int height, Character player, Character player2) {
        this.width = width;
        this.height = height;
        this.player = player;
        this.collectedKeys = new ArrayList<>();
        this.grid = new Box[width][height];
        this.door = null;

        // Inicializar el tablero con casillas vacías
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Box(i, j);
            }
        }

        // Ubicar al jugador en una celda inicial (ejemplo: arriba a la izquierda)
        grid[0][0].setCharacter(player);
        player.setPosition(0, 0);

        // Ubicar al jugador 2en una celda inicial (ejemplo: arriba a la izquierda)
        grid[0][5].setCharacter(player2);
        player2.setPosition(0, 5);
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

    public void movePlayer(int newX, int newY) {
        if (isMoveValid(newX, newY)) {
            // Eliminar al jugador de la casilla actual
            Box currentBox = getBox(player.getX(), player.getY());
            if (currentBox != null) {
                currentBox.removeCharacter();
            }

            // Moverlo a la nueva casilla
            Box newBox = getBox(newX, newY);
            if (newBox != null) {
                newBox.setCharacter(player);
                player.setPosition(newX, newY);
                collectKey(newBox);
            }
        }
    }

    private void collectKey(Box box) {
        if (box.hasKey() && box.getKey().canBePickedBy(player)) {
            collectedKeys.add(box.getKey());
            System.out.println("Llave recogida: " + box.getKey().getClass().getSimpleName());

            // Eliminar la llave del tablero
            box.removeKey();

            // Verificar si se han recogido todas las llaves
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
            System.out.println("¡Todas las llaves han sido recogidas! La puerta se ha abierto.");
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

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public Box[][] getGrid() {
        return grid;
    }

    public List<Key> getCollectedKeys() {
        return collectedKeys;
    }

    public Character getPlayer() {
        return player;
    }

    public Character getPlayer2(){
        return this.player2;
    }

    public void setPlayer(Character player) {
        this.player = player;
    }

    public Door getDoor() {
        return door;
    }
}
