package escuelaing.edu.co.bakend_gl.model.dto;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.board.Box;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO para transmitir el estado del tablero a través de WebSocket.
 * Contiene sólo la información necesaria para renderizar el tablero en el frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardStateDto {
    private String boardId;
    private String roomCode;
    private int width;
    private int height;
    private List<BoxDto> boxes = new ArrayList<>();
    private List<CharacterStateDto> characters = new ArrayList<>();
    private List<KeyDto> collectedKeys = new ArrayList<>();
    private DoorDto door;
    private String actionType; // Tipo de acción: "move", "build", "destroy", "start"
    private long timestamp;

    /**
     * Construye un DTO a partir de un objeto Board
     */
    public static BoardStateDto fromBoard(Board board, String roomCode, String actionType) {
        BoardStateDto dto = new BoardStateDto();
        dto.setBoardId(board.getId());
        dto.setRoomCode(roomCode);
        dto.setWidth(board.getWidth());
        dto.setHeight(board.getHeight());
        dto.setActionType(actionType);
        dto.setTimestamp(System.currentTimeMillis());

        // Agregar sólo las cajas que son relevantes (tienen contenido o son caminables)
        Box[][] grid = board.getGrid();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Box box = grid[i][j];
                if (box != null) {
                    // Solo incluir cajas con contenido o que sean caminables
                    if (box.getBlock() != null || box.getCharacter() != null ||
                            box.getKey() != null || box.getDoor() != null) {
                        dto.getBoxes().add(BoxDto.fromBox(box));
                    }
                }
            }
        }

        // Agregar personajes
        for (Character character : board.getCharacters()) {
            dto.getCharacters().add(CharacterStateDto.fromCharacter(character));
        }

        // Agregar puerta si existe
        if (board.getDoor() != null) {
            dto.setDoor(DoorDto.fromDoor(board.getDoor()));
        }

        return dto;
    }

    /**
     * DTO para una caja (celda) del tablero
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxDto {
        private int x;
        private int y;
        private Map<String, Object> block; // Tipo y propiedades del bloque
        private boolean walkable;
        private Map<String, Object> key; // Datos de la llave si hay una

        public static BoxDto fromBox(Box box) {
            BoxDto dto = new BoxDto();
            dto.setX(box.getX());
            dto.setY(box.getY());
            dto.setWalkable(box.isWalkable());

            // Agregar info del bloque si existe
            if (box.getBlock() != null) {
                Map<String, Object> blockInfo = new HashMap<>();
                blockInfo.put("type", box.getBlock().getType());
                blockInfo.put("destructible", box.getBlock().isDestructible());
                dto.setBlock(blockInfo);
            }

            // Agregar info de la llave si existe
            if (box.getKey() != null) {
                Map<String, Object> keyInfo = new HashMap<>();
                keyInfo.put("type", box.getKey().getClass().getSimpleName());
                dto.setKey(keyInfo);
            }

            return dto;
        }
    }

    /**
     * DTO para el estado de un personaje
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterStateDto {
        private String playerId;
        private String characterType;
        private int x;
        private int y;
        private String direction;
        private String element;

        public static CharacterStateDto fromCharacter(Character character) {
            CharacterStateDto dto = new CharacterStateDto();
            dto.setPlayerId(character.getPlayerId());
            dto.setCharacterType(character.getClass().getSimpleName());
            dto.setX(character.getX());
            dto.setY(character.getY());
            dto.setDirection(character.getDirection());
            dto.setElement(character.getElement());
            return dto;
        }
    }

    /**
     * DTO para llaves recogidas
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyDto {
        private String type;
        private String element;
    }

    /**
     * DTO para la puerta
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoorDto {
        private int x;
        private int y;
        private boolean locked;

        public static DoorDto fromDoor(escuelaing.edu.co.bakend_gl.model.board.Door door) {
            DoorDto dto = new DoorDto();
            dto.setX(door.getX());
            dto.setY(door.getY());
            dto.setLocked(door.isLocked());
            return dto;
        }
    }
} 