package escuelaing.edu.co.bakend_gl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.dto.ActionPlayerDto;
import escuelaing.edu.co.bakend_gl.model.dto.BoardStateDto;
import escuelaing.edu.co.bakend_gl.services.BoardService;
import escuelaing.edu.co.bakend_gl.services.GameService;
import escuelaing.edu.co.bakend_gl.model.characters.Character;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final GameService gameService;

    @Autowired
    public BoardController(BoardService boardService, GameService gameService) {
        this.boardService = boardService;
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<Board> createBoard(@RequestParam int width, @RequestParam int height) {
        Board newBoard = boardService.createBoard(width, height);
        
        // Guardar también en Redis con tiempo de expiración de 1 hora
        boardService.saveWithExpiration(newBoard, 1, TimeUnit.HOURS);
        
        return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable("id") String id) {
        // Primero intentamos obtener de Redis por rendimiento
        Board board = boardService.getBoardFromRedis(id);
        
        // Si no está en Redis, buscamos en el repositorio principal
        if (board == null) {
            Optional<Board> optionalBoard = boardService.getBoardById(id);
            if (optionalBoard.isPresent()) {
                board = optionalBoard.get();
                // Lo guardamos en Redis para futuras consultas
                boardService.saveWithExpiration(board, 1, TimeUnit.HOURS);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Board> updateBoard(@RequestBody Board board) {
        if (board.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Board updatedBoard = boardService.updateBoard(board);
        
        // Actualizamos también en Redis
        boardService.saveWithExpiration(updatedBoard, 1, TimeUnit.HOURS);
        
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") String id) {
        boardService.deleteBoard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<BoardStateDto> getBoardState(@PathVariable String roomCode) {
        log.info("Obteniendo estado del tablero para sala: {}", roomCode);
        
        Board board = gameService.getBoardByRoomCode(roomCode);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        
        BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "state");
        return ResponseEntity.ok(boardState);
    }
    
    /**
     * Endpoint para probar el movimiento de un personaje
     */
    @PostMapping("/{roomCode}/move")
    public ResponseEntity<BoardStateDto> movePlayer(
            @PathVariable String roomCode,
            @RequestBody ActionPlayerDto actionPlayerDto) {
            
        log.info("Moviendo jugador {} en dirección {} en sala {}", 
                actionPlayerDto.getPlayerId(), actionPlayerDto.getDirection(), roomCode);
                
        // Ejecutar el movimiento
        gameService.movePlayer(roomCode, actionPlayerDto.getPlayerId(), actionPlayerDto.getDirection());
        
        // Obtener estado actualizado
        Board board = gameService.getBoardByRoomCode(roomCode);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        
        BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "move");
        return ResponseEntity.ok(boardState);
    }
    
    /**
     * Endpoint para probar la recogida de una llave (simulando movimiento a una posición con llave)
     */
    @PostMapping("/{roomCode}/test-collect-key")
    public ResponseEntity<String> testCollectKey(
            @PathVariable String roomCode,
            @RequestParam String playerId,
            @RequestParam int keyX,
            @RequestParam int keyY) {
            
        Board board = gameService.getBoardByRoomCode(roomCode);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Buscar el personaje
        Character player = null;
        for (Character character : board.getCharacters()) {
            if (playerId.equals(character.getPlayerId())) {
                player = character;
                break;
            }
        }
        
        if (player == null) {
            return ResponseEntity.badRequest().body("Jugador no encontrado");
        }
        
        // Mover el personaje a la posición de la llave
        board.movePlayer(player, keyX, keyY);
        
        // Guardar cambios
        boardService.updateBoard(board);
        
        return ResponseEntity.ok("Personaje movido a posición de llave: (" + keyX + "," + keyY + ")");
    }

    /**
     * Endpoint para crear un tablero de prueba con un roomCode específico
     */
    @PostMapping("/create-test")
    public ResponseEntity<BoardStateDto> createTestBoard(
            @RequestParam String roomCode,
            @RequestParam(defaultValue = "20") int width,
            @RequestParam(defaultValue = "20") int height) {
        
        log.info("Creando tablero de prueba con roomCode: {}", roomCode);
        
        // Crear un tablero para una sala específica (simulando una partida) 
        // Creamos un tablero básico con las dimensiones especificadas
        Board board = new Board(width, height);
        board.setId(roomCode); // Usamos exactamente el roomCode proporcionado
        
        // Agregar un personaje de prueba (id arbitrario para pruebas)
        String testPlayerId = "test-player-1";
        escuelaing.edu.co.bakend_gl.model.characters.Character character = 
            new escuelaing.edu.co.bakend_gl.model.characters.Flame(1, 1);
        character.setPlayerId(testPlayerId);
        board.addCharacter(character);
        
        // Colocar algunas llaves de prueba
        board.getBox(3, 2).setKey(new escuelaing.edu.co.bakend_gl.model.keys.KeyFlame(3, 2));
        board.getBox(16, 3).setKey(new escuelaing.edu.co.bakend_gl.model.keys.KeyAqua(16, 3));
        
        // Colocar una puerta
        board.placeDoor(10, 6);
        
        // Guardar en Redis con expiración
        boardService.saveWithExpiration(board, 2, TimeUnit.HOURS);
        
        // Convertir a DTO y retornar
        BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "create-test");
        return ResponseEntity.ok(boardState);
    }

    /**
     * Endpoint para limpiar Redis en caso de problemas de serialización
     */
    @PostMapping("/clear-redis")
    public ResponseEntity<String> clearRedisBoards() {
        log.info("Limpiando todos los tableros de Redis");
        boardService.clearRedisBoards();
        return ResponseEntity.ok("Redis limpiado correctamente.");
    }
} 