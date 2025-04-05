package escuelaing.edu.co.bakend_gl.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.blocks.*;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.*;
import escuelaing.edu.co.bakend_gl.model.keys.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private Board board;
    private List<Character> players;
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

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
        this.board = new Board(10, 10, players);

        // Mapa estático inicial (temporal para la primera entrega)
        board.addBlock(new BlockIron(3, 3));
        board.addBlock(new BlockIron(4, 4));
        board.addBlock(new BlockIron(5, 5));

        board.addBlock(new BlockWater(2, 5));
        board.addBlock(new BlockFire(7, 7));

        board.getBox(3, 3).setKey(new KeyFlame(3, 3));
        board.getBox(6, 6).setKey(new KeyAqua(6, 6));
        board.getBox(7, 2).setKey(new KeyStone(7, 2));
        board.getBox(1, 8).setKey(new KeyBrisa(1, 8));

        board.placeDoor(9, 9);
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
}
