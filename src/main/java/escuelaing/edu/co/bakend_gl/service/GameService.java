package escuelaing.edu.co.bakend_gl.service;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.blocks.BlockIron;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {
    private Board board;

    public GameService() {
        List<BlockIron> blocks = new ArrayList<>();
        blocks.add(new BlockIron(5, 5)); // Bloques iniciales en el tablero
        blocks.add(new BlockIron(6, 5));

        Character player = new Character(2, 2);
        board = new Board(10, 10, blocks, player);
    }

    public void movePlayer(String direction) {
        Character player = board.getPlayer();
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

    public Board getBoard() {
        return board;
    }
}
