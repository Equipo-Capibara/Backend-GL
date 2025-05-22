package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.blocks.*;
import escuelaing.edu.co.bakend_gl.model.board.Box;
import escuelaing.edu.co.bakend_gl.model.board.Door;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.*;
import escuelaing.edu.co.bakend_gl.model.keys.*;
import escuelaing.edu.co.bakend_gl.model.basic_components.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class GameService {

    private int currentLevel = 1;

    // Solo mantenemos un mapa para relacionar jugadores con personajes
    // Este mapa se usará solo temporalmente durante las operaciones
    private final Map<String, Character> characterByPlayerId = new ConcurrentHashMap<>();

    // Mapa de locks por sala para sincronización
    private final Map<String, Object> roomLocks = new ConcurrentHashMap<>();

    @Autowired
    private BoardService boardService;

    public GameService() {
        // Nada que inicializar al arranque
    }

    public void initializeGameForRoom(String roomCode, List<Player> players) {
        Board board = setupLevel(currentLevel);

        // Usamos el roomCode como id del tablero
        board.setId(roomCode);

        // Asignar posiciones iniciales a los personajes
        int[][] spawnPoints = {
                {1, 1}, {18, 1}, {1, 10}, {18, 10}
        };

        for (int i = 0; i < players.size() && i < spawnPoints.length; i++) {
            Player player = players.get(i);
            int x = spawnPoints[i][0];
            int y = spawnPoints[i][1];

            CharacterType type = getCharacterTypeById(player.getCharacter());
            Character character = createCharacterFromType(type, x, y);

            if (character != null) {
                // Identificamos el personaje con el ID del jugador
                character.setPlayerId(player.getId());
                board.addCharacter(character);
            }
        }

        // Guardar el tablero solo en Redis
        boardService.saveWithExpiration(board, 2, TimeUnit.HOURS); // Expire en 2 horas

        // Creamos el lock para esta sala
        roomLocks.put(roomCode, new Object());

        log.info("Tablero inicializado para sala: {} con {} jugadores", roomCode, players.size());
    }

    private CharacterType getCharacterTypeById(String id) {
        for (CharacterType type : CharacterType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return CharacterType.FLAME; // Default en caso de error
    }

    private Character createCharacterFromType(CharacterType type, int x, int y) {
        switch (type) {
            case FLAME: return new Flame(x, y);
            case AQUA: return new Aqua(x, y);
            case BRISA: return new Brisa(x, y);
            case STONE: return new Stone(x, y);
            default: return null;
        }
    }

    private Board setupLevel(int level) {
        switch (level) {
            case 1: return setupLevel1();
            case 2: return setupLevel2();
            default: throw new IllegalArgumentException("Nivel no válido.");
        }
    }

    private Board setupLevel1() {
        Board board = new Board(20, 12);
        createBorders(board);

        // Llaves
        board.getBox(9, 5).setKey(new KeyFlame(3, 2));
        board.getBox(14, 7).setKey(new KeyAqua(16, 3));
        board.getBox(3, 2).setKey(new KeyStone(5, 10));
        board.getBox(4, 9).setKey(new KeyBrisa(13, 9));

        // Obstáculos
        board.addBlock(new BlockIron(7, 3));
        board.addBlock(new BlockIron(8, 3));
        board.addBlock(new BlockIron(9, 3));
        board.addBlock(new BlockIron(10, 3));
        board.addBlock(new BlockIron(11, 3));
        board.addBlock(new BlockIron(11, 4));
        board.addBlock(new BlockIron(7, 4));
        board.addBlock(new BlockIron(7, 5));
        board.addBlock(new BlockIron(7, 6));
        board.addBlock(new BlockIron(14, 6));
        board.addBlock(new BlockIron(15, 6));
        board.addBlock(new BlockIron(16, 6));
        board.addBlock(new BlockIron(17, 6));
        board.addBlock(new BlockIron(10, 10));
        board.addBlock(new BlockIron(9, 7));

        // prueba nuevos bloques
        board.addBlock(new BlockWater(4, 1));
        board.addBlock(new BlockWater(4, 2));
        board.addBlock(new BlockWater(4, 3));
        board.addBlock(new BlockAir(3, 3));
        board.addBlock(new BlockAir(2, 3));
        board.addBlock(new BlockEarth(2, 4));
        board.addBlock(new BlockFire(2, 5));
        board.addBlock(new BlockFire(1, 5));
        board.addBlock(new BlockWater(15, 1)); //
        board.addBlock(new BlockEarth(15, 2));
        board.addBlock(new BlockEarth(15, 3));
        board.addBlock(new BlockFire(11, 5)); //
        board.addBlock(new BlockFire(11, 6));
        board.addBlock(new BlockFire(11, 7));
        board.addBlock(new BlockWater(10, 7));
        board.addBlock(new BlockWater(8, 7));
        board.addBlock(new BlockAir(7, 7));
        board.addBlock(new BlockAir(18, 6)); //
        board.addBlock(new BlockAir(10, 9));
        board.addBlock(new BlockWater(11, 9));
        board.addBlock(new BlockWater(12, 9));
        board.addBlock(new BlockFire(13, 9));
        board.addBlock(new BlockWater(14, 9));
        board.addBlock(new BlockFire(2, 8)); //
        board.addBlock(new BlockWater(3, 8));
        board.addBlock(new BlockWater(4, 8));
        board.addBlock(new BlockAir(5, 8));
        board.addBlock(new BlockEarth(5, 9));
        board.addBlock(new BlockFire(5, 10));

        return board;
    }

    private Board setupLevel2() {
        Board board = new Board(20, 12);
        createBorders(board);

        board.getBox(2, 2).setKey(new KeyAqua(2, 2));
        board.getBox(8, 8).setKey(new KeyBrisa(8, 8));
        board.getBox(5, 6).setKey(new KeyFlame(5, 6));
        board.getBox(16, 10).setKey(new KeyStone(16, 10));

        for (int x = 5; x <= 15; x++) {
            board.addBlock(new BlockIron(x, 5));
        }

        board.placeDoor(10, 6);

        return board;
    }

    private void createBorders(Board board) {
        for (int x = 0; x < 20; x++) {
            board.addBlock(new BlockIron(x, 0));
            board.addBlock(new BlockIron(x, 11));
        }
        for (int y = 0; y < 12; y++) {
            board.addBlock(new BlockIron(0, y));
            board.addBlock(new BlockIron(19, y));
        }
    }

    public void movePlayer(String roomCode, String playerId, String direction) {
        // Obtenemos el lock para esta operación
        Object lock = roomLocks.computeIfAbsent(roomCode, k -> new Object());

        synchronized (lock) {
            // Obtenemos el tablero directamente desde Redis
            Board board = boardService.getBoardFromRedis(roomCode);
            if (board == null) {
                log.error("No se encontró el tablero para la sala: {}", roomCode);
                return;
            }

            // Buscamos el personaje del jugador
            Character player = findPlayerCharacter(board, playerId);
            if (player == null) {
                log.error("No se encontró el personaje para el jugador: {}", playerId);
                return;
            }

            int newX = player.getX();
            int newY = player.getY();

            switch (direction.toLowerCase()) {
                case "w": newY--; player.setDirection("w"); break;
                case "s": newY++; player.setDirection("s"); break;
                case "a": newX--; player.setDirection("a"); break;
                case "d": newX++; player.setDirection("d"); break;
            }

            // Verificar si el movimiento es válido
            if (board.isMoveValid(newX, newY)) {
                // Guardar posición actual antes de mover
                int oldX = player.getX();
                int oldY = player.getY();

                // Ejecutar el movimiento en el tablero
                board.movePlayer(player, newX, newY);

                // Verificar si el jugador llegó a la puerta
                Box newBox = board.getBox(newX, newY);
                if (newBox != null && newBox.getDoor() != null) {
                    Door door = newBox.getDoor();
                    if (!door.isLocked()) {
                        // Puerta desbloqueada, nivel completado
                        log.info("Jugador {} alcanzó la puerta desbloqueada. ¡Nivel completado!", playerId);
                        // Aquí puedes implementar lógica para pasar al siguiente nivel o terminar el juego
                    }
                }

                log.info("Jugador {} se movió de ({},{}) a ({},{})", playerId, oldX, oldY, newX, newY);
            } else {
                log.info("Movimiento no válido para el jugador {} a la posición ({},{})", playerId, newX, newY);
            }

            // Actualizar en Redis después de cada movimiento
            boardService.updateBoard(board);
        }
    }

    public void buildBlocks(String roomCode, String playerId) {
        // Obtenemos el lock para esta operación
        Object lock = roomLocks.computeIfAbsent(roomCode, k -> new Object());

        synchronized (lock) {
            // Obtenemos el tablero directamente desde Redis
            Board board = boardService.getBoardFromRedis(roomCode);
            if (board == null) {
                log.error("No se encontró el tablero para la sala: {}", roomCode);
                return;
            }

            // Buscamos el personaje del jugador
            Character player = findPlayerCharacter(board, playerId);
            if (player == null) {
                log.error("No se encontró el personaje para el jugador: {}", playerId);
                return;
            }

            int dx = 0;
            int dy = 0;
            switch (player.getDirection().toLowerCase()) {
                case "w": dy = -1; break;
                case "s": dy = 1; break;
                case "a": dx = -1; break;
                case "d": dx = 1; break;
                default: return;
            }

            int x = player.getX();
            int y = player.getY();
            String elementBlock = player.getElement();

            while (true) {
                x += dx;
                y += dy;

                Box box = board.getBox(x, y);
                if (box == null) break;

                synchronized (box) {
                    if (box.getBlock() != null || box.getCharacter() != null) break;

                    switch (elementBlock) {
                        case "Flame": board.addBlock(new BlockFire(x, y)); break;
                        case "Aqua": board.addBlock(new BlockWater(x, y)); break;
                        case "Stone": board.addBlock(new BlockEarth(x, y)); break;
                        case "Brisa": board.addBlock(new BlockAir(x, y)); break;
                        default: break;
                    }
                }
            }

            // Actualizar en Redis después de construir bloques
            boardService.updateBoard(board);
        }
    }

    public void destroyBlock(String roomCode, String playerId) {
        // Obtenemos el lock para esta operación
        Object lock = roomLocks.computeIfAbsent(roomCode, k -> new Object());

        synchronized (lock) {
            // Obtenemos el tablero directamente desde Redis
            Board board = boardService.getBoardFromRedis(roomCode);
            if (board == null) {
                log.error("No se encontró el tablero para la sala: {}", roomCode);
                return;
            }

            // Buscamos el personaje del jugador
            Character player = findPlayerCharacter(board, playerId);
            if (player == null) {
                log.error("No se encontró el personaje para el jugador: {}", playerId);
                return;
            }

            int dx = 0;
            int dy = 0;
            switch (player.getDirection().toLowerCase()) {
                case "w": dy = -1; break;
                case "s": dy = 1; break;
                case "a": dx = -1; break;
                case "d": dx = 1; break;
                default: return;
            }

            int x = player.getX();
            int y = player.getY();
            String elementBlock = player.getElement();

            while (true) {
                x += dx;
                y += dy;

                Box box = board.getBox(x, y);
                if (box == null) break;

                synchronized (box) {
                    if (box.getCharacter() != null) break;

                    Block block = box.getBlock();
                    if (block != null) {
                        if (isBlockOfPlayerElement(elementBlock, block)) {
                            board.removeBlock(x, y);
                        } else {
                            break; // Bloque de otro tipo
                        }
                    } else {
                        break; // No hay más bloques
                    }
                }
            }

            // Actualizar en Redis después de destruir bloques
            boardService.updateBoard(board);
        }
    }

    private boolean isBlockOfPlayerElement(String elementBlock, Block block) {
        return (elementBlock.equals("Flame") && block instanceof BlockFire) ||
                (elementBlock.equals("Aqua") && block instanceof BlockWater) ||
                (elementBlock.equals("Stone") && block instanceof BlockEarth) ||
                (elementBlock.equals("Brisa") && block instanceof BlockAir);
    }

    public void useAbility(String roomCode, String playerId) {
        // Implementar uso de habilidades especiales de los personajes
    }

    /**
     * Busca un personaje en el tablero por el ID del jugador
     */
    private Character findPlayerCharacter(Board board, String playerId) {
        for (Character character : board.getCharacters()) {
            if (playerId.equals(character.getPlayerId())) {
                return character;
            }
        }
        return null;
    }

    /**
     * Obtiene el tablero para una sala directamente desde Redis
     */
    public Board getBoard(String roomCode) {
        return boardService.getBoardFromRedis(roomCode);
    }

    /**
     * Obtiene el tablero para una sala directamente desde Redis
     */
    public Board getBoardByRoomCode(String roomCode) {
        return boardService.getBoardFromRedis(roomCode);
    }

    public void switchLevel(int level) {
        if (level > 0) {
            currentLevel = level;
        }
    }
}
