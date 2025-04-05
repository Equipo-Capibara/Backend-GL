package escuelaing.edu.co.bakend_gl.model.board;

import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.keys.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Board {
    private int width;
    private int height;
    private Box[][] grid;
    private List<Key> collectedKeys;
    private List<Character> players;
    private Door door;

    public Board(int width, int height, List<Character> players) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.collectedKeys = new ArrayList<>();
        this.grid = new Box[width][height];
        this.door = null;

        // Inicializar el tablero con casillas vacías
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Box(i, j);
            }
        }

        // Ubicar a los jugadores en posiciones iniciales
        int[][] startPositions = {{0, 0}, {9, 9}, {0, 9}, {9, 0}};
        for (int i = 0; i < players.size(); i++) {
            int x = startPositions[i][0];
            int y = startPositions[i][1];
            grid[x][y].setCharacter(players.get(i));
            players.get(i).setPosition(x, y);
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
                collectKey(player, newBox);
            }
        }
    }

    private void collectKey(Character player, Box box) {
        if (box.hasKey() && box.getKey().canBePickedBy(player)) {
            collectedKeys.add(box.getKey());
            player.pickUpKey(); // Decimos que el jugador tiene la llave
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

    public List<Character> getPlayers() {return players; }

    public Door getDoor() {
        return door;
    }

}
