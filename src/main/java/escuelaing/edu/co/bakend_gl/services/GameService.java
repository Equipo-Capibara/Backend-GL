package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.blocks.*;
import escuelaing.edu.co.bakend_gl.model.board.Box;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.*;
import escuelaing.edu.co.bakend_gl.model.keys.*;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private int currentLevel = 1;
    private Map<String, Board> boardsByRoom = new ConcurrentHashMap<>();
    private Map<String, Character> characterByPlayerId = new ConcurrentHashMap<>();
    private final Map<String, Object> roomLocks = new ConcurrentHashMap<>();


    public GameService() {
        // Nada que inicializar al arranque
    }

    public void initializeGameForRoom(String roomCode, List<Player> players) {
        Board board = setupLevel(currentLevel);

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
                board.addCharacter(character);
                characterByPlayerId.put(player.getId(), character);
            }
        }

        boardsByRoom.put(roomCode, board);
        roomLocks.put(roomCode, new Object()); // Lock para esa sala

        if(!roomLocks.isEmpty()){
            System.out.println("A continuacion se ve el codigo de la sala si room locks no esta vacio" + roomLocks.get(roomCode));
        } else{
            System.out.println("Room locks esta vacio");
        }
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

        board.placeDoor(10, 6);

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
        Board board = boardsByRoom.get(roomCode);
        Character player = characterByPlayerId.get(playerId);
        Object lock = roomLocks.get(roomCode);

        if (board == null || player == null) return;

        synchronized (lock) {
            int newX = player.getX();
            int newY = player.getY();

            switch (direction.toLowerCase()) {
                case "w": newY--; player.setDirection("w"); break;
                case "s": newY++; player.setDirection("s"); break;
                case "a": newX--; player.setDirection("a"); break;
                case "d": newX++; player.setDirection("d"); break;
            }

            board.movePlayer(player, newX, newY);
        }
    }

    public void buildBlocks(String roomCode, String playerId) {
        Board board = boardsByRoom.get(roomCode);
        Character player = characterByPlayerId.get(playerId);
        Object lock = roomLocks.get(roomCode);

        if (board == null || player == null) return;

        synchronized (lock) {
            int dx = 0, dy = 0;
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
                    }
                }
            }
        }
    }

    public void destroyBlock(String roomCode, String playerId) {
        Board board = boardsByRoom.get(roomCode);
        Character player = characterByPlayerId.get(playerId);
        Object lock = roomLocks.get(roomCode);

        if (board == null || player == null) return;

        synchronized (lock) {
            int dx = 0, dy = 0;
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
        }
    }

    private boolean isBlockOfPlayerElement (String elementBlock, Block block) {
        switch (elementBlock) {
            case "Flame": return block instanceof BlockFire;
            case "Aqua": return block instanceof BlockWater;
            case "Stone": return block instanceof BlockEarth;
            case "Brisa": return block instanceof BlockAir;
            default: return false;
        }
    }


    public void useAbility(String roomCode, String playerId) {
        Character player = characterByPlayerId.get(playerId);
        if (player != null) {
            player.useAbility();
        }
    }

    public Board getBoard(String roomCode) {
        return boardsByRoom.get(roomCode);
    }

    public void switchLevel(int level) {
        currentLevel = level;
    }
}
