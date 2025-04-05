package escuelaing.edu.co.bakend_gl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.blocks.*;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.*;
import escuelaing.edu.co.bakend_gl.model.keys.*;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Board board;
    private Character player;
    private int currentLevel = 1;

    public GameService() {
        loadLevel(currentLevel);
    }

    private void loadLevel(int level) {
        switch (level) {
            case 1:
                setupLevel1();
                break;
            case 2:
                setupLevel2();
                break;
            default:
                throw new IllegalArgumentException("Nivel no válido.");
        }
    }

    private void setupLevel1() {
        player = new Flame(1, 1); // esquina superior izquierda
        board = new Board(20, 12, player);
        createBorders();

        board.addCharacter(new Aqua(18, 1));  // ya no está encima del borde
        board.addCharacter(new Brisa(1, 10));
        board.addCharacter(new Stone(18, 10));

        // Llaves
        board.getBox(3, 2).setKey(new KeyFlame(3, 2));
        board.getBox(16, 3).setKey(new KeyAqua(16, 3));
        board.getBox(5, 10).setKey(new KeyStone(5, 10));
        board.getBox(13, 9).setKey(new KeyBrisa(13, 9));

        // Obstáculos
        board.addBlock(new BlockIron(10, 0));
        board.addBlock(new BlockIron(10, 1));
        board.addBlock(new BlockIron(10, 2));
        board.addBlock(new BlockIron(10, 3));
        board.addBlock(new BlockIron(10, 4));
        board.addBlock(new BlockIron(10, 5));

        board.placeDoor(10, 6); // Puerta al centro-ish
    }

    private void setupLevel2() {
        player = new Aqua(1, 1);
        board = new Board(20, 12, player);
        createBorders();

        board.addCharacter(new Flame(18, 1));
        board.addCharacter(new Brisa(1, 10));
        board.addCharacter(new Stone(18, 10));

        // Llaves en otras posiciones
        board.getBox(2, 2).setKey(new KeyAqua(2, 2));
        board.getBox(8, 8).setKey(new KeyBrisa(8, 8));
        board.getBox(5, 6).setKey(new KeyFlame(5, 6));
        board.getBox(16, 10).setKey(new KeyStone(16, 10));

        // Obstáculos distintos
        for (int x = 5; x <= 15; x++) {
            board.addBlock(new BlockIron(x, 5));
        }

        board.placeDoor(10, 6);
    }

    private void createBorders() {
        for (int x = 0; x < 20; x++) {
            board.addBlock(new BlockIron(x, 0));              // arriba
            board.addBlock(new BlockIron(x, 11));             // abajo
        }
        for (int y = 0; y < 12; y++) {
            board.addBlock(new BlockIron(0, y));              // izquierda
            board.addBlock(new BlockIron(19, y));             // derecha
        }
    }

    public void movePlayer(String direction) {
        int newX = player.getX();
        int newY = player.getY();

        switch (direction.toLowerCase()) {
            case "w": newY--; break;
            case "s": newY++; break;
            case "a": newX--; break;
            case "d": newX++; break;
        }
        board.movePlayer(newX, newY);
    }

    public void useAbility() {
        player.useAbility();
    }

    public Board getBoard() {
        return board;
    }

    public void switchLevel(int level) {
        currentLevel = level;
        loadLevel(level);
    }
}