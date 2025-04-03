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

    public GameService() {
        String characterType = "fire";
        switch (characterType.toLowerCase()) {
            case "fire":  player = new Flame(2, 2); break;
            case "water": player = new Aqua(2, 2); break;
            case "air":   player = new Brisa(2, 2); break;
            case "earth": player = new Stone(2, 2); break;
            default:      throw new IllegalArgumentException("Tipo de personaje no válido.");
        }

        //nuevo jugador
        Character player2 = new Aqua(0, 5);

        // Inicializar el tablero
        board = new Board(10, 10, player, player2);

        board.addBlock(new BlockIron(3, 3));
        board.addBlock(new BlockIron(4, 4));
        board.addBlock(new BlockIron(5, 5));
        //board.addBlock(new BlockIron(0, 5));



        // Agregar bloques de agua
        board.addBlock(new BlockWater(2, 5));

        // Agregar bloques de fuego
        board.addBlock(new BlockFire(7, 7));

        // Colocar llaves en el tablero
        board.getBox(3, 3).setKey(new KeyFlame(3, 3));   // Llave de fuego
        board.getBox(6, 6).setKey(new KeyAqua(6, 6));   // Llave de agua
        board.getBox(7, 2).setKey(new KeyStone(7, 2));  // Llave de tierra
        board.getBox(1, 8).setKey(new KeyBrisa(1, 8));    // Llave de aire

        // Colocar la puerta en el tablero
        board.placeDoor(9, 9);

        System.out.println("----------------------------------------");
        createBlock();
        System.out.println("----------------------------------------");
    }

    public void movePlayer(String direction) {
        int newX = player.getX();
        int newY = player.getY();
        // int newY = board.getPlayer.getY();

        switch (direction.toLowerCase()) {
            case "w": newY--; break;
            case "s": newY++; break;
            case "a": newX--; break;
            case "d": newX++; break;
        }
        board.movePlayer(newX, newY);
        board.getPlayer().setDirectionView(direction);
    }

    public void createBlock() {
        int x = player.getX();
        int y = player.getY();

        // Determinar la dirección de construcción
        switch (player.getDirectionView()) {
            case "w": y--; break; // Arriba
            case "s": y++; break; // Abajo
            case "a": x--; break; // Izquierda
            case "d": x++; break; // Derecha
        }

        // Mientras no haya un bloque o personaje, sigue construyendo
        while (board.isMoveValid(x, y) && board.getBox(x, y).getBlock() == null && board.getBox(x, y).getCharacter() == null) {
            Block newBlock = new BlockFire(x, y);
            board.addBlock(newBlock);
            System.out.println("Bloque creado en: (" + x + ", " + y + ")");

            // Mover la posición en la misma dirección
            switch (player.getDirectionView()) {
                case "w": y--; break;
                case "s": y++; break;
                case "a": x--; break;
                case "d": x++; break;
            }
        }
    }


    public Board getBoard() {
        return board;
    }
}
