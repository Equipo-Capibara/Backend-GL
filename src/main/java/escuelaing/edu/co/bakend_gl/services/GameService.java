package escuelaing.edu.co.bakend_gl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.blocks.*;
import escuelaing.edu.co.bakend_gl.model.board.Door;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.*;
import escuelaing.edu.co.bakend_gl.model.keys.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private Board board;
    private List<Character> players;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();
    private int currentLevel = 1;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Aca esto esta quemado en codigo
    public GameService() {
        System.out.println("Inicializando GameService...");
        initGame(List.of(
                new Flame("1",0, 0),
                new Aqua("2",9, 0),
                new Stone("3",0, 9),
                new Brisa("4",9, 9)
        ));
    }


    public void initGame(List<Character> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException("El juego debe tener exactamente 4 jugadores.");
        }
        this.players = players;
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

    public void movePlayer(String playerId, String direction) {
        // Buscar al jugador por su ID
        Character player = players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        int newX = player.getX();
        int newY = player.getY();

        switch (direction.toLowerCase()) {
            case "w": newY--; break;
            case "s": newY++; break;
            case "a": newX--; break;
            case "d": newX++; break;
            default:
                throw new IllegalArgumentException("Dirección inválida: " + direction);
        }

        if (board.isMoveValid(newX, newY)) {
            board.movePlayer(player, newX, newY);
            player.setDirectionView(direction);

            // Verifica si está en la puerta con la llave
            Door door = board.getDoor();
            if (!door.isLocked() && player.isHasKey() && player.getX() == door.getX() && player.getY() == door.getY()) {
                // Elimina al jugador del tablero
                board.getBox(door.getX(), door.getY()).removeCharacter();
                board.getPlayers().remove(player);
                System.out.println("Jugador " + player.getId() + " ha salido por la puerta");

                // Verifica si el juego terminó
                if (board.getPlayers().isEmpty()) {
                    messagingTemplate.convertAndSend("/topic/game-finished", "¡Juego terminado!");
                }
            }
        }

    }

    public void createBlock(String playerId) {
        Character player = players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        int x = player.getX();
        int y = player.getY();

        switch (player.getDirectionView()) {
            case "w": y--; break;
            case "s": y++; break;
            case "a": x--; break;
            case "d": x++; break;
        }

        String key = x + "," + y;
        locks.putIfAbsent(key, new Object());

        synchronized (locks.get(key)) {
            while (board.isMoveValid(x, y) && board.getBox(x, y).getBlock() == null && board.getBox(x, y).getCharacter() == null) {
                board.addBlock(new BlockFire(x, y));

                switch (player.getDirectionView()) {
                    case "w": y--; break;
                    case "s": y++; break;
                    case "a": x--; break;
                    case "d": x++; break;
                }
                key = x + "," + y;
                locks.putIfAbsent(key, new Object());
            }
        }
    }

    public void destroyBlock(String playerId) {
        Character player = players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Jugador no encontrado"));

        int x = player.getX();
        int y = player.getY();

        // Moverse en la dirección en la que está mirando el jugador
        switch (player.getDirectionView()) {
            case "w": y--; break;
            case "s": y++; break;
            case "a": x--; break;
            case "d": x++; break;
        }

        String key = x + "," + y;
        locks.putIfAbsent(key, new Object());

        synchronized (locks.get(key)) {
            Block initialBlock = board.getBox(x, y).getBlock();
            if (initialBlock == null || !initialBlock.isDestructible() || !initialBlock.getAllowedCharacter().equals(player.getClass().getSimpleName())) {
                return; // No hay bloque, no es destructible o el jugador no puede destruirlo
            }

            String blockType = initialBlock.getType();

            // Recorremos en la dirección hasta encontrar un bloque distinto o vacío
            while (board.getBox(x, y) != null && board.getBox(x, y).getBlock() != null &&
                    board.getBox(x, y).getBlock().getType().equals(blockType)) {

                board.removeBlock(x, y);

                switch (player.getDirectionView()) {
                    case "w": y--; break;
                    case "s": y++; break;
                    case "a": x--; break;
                    case "d": x++; break;
                }

                key = x + "," + y;
                locks.putIfAbsent(key, new Object());
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public void switchLevel(int level) {
        currentLevel = level;
        loadLevel(level);
    }
}

