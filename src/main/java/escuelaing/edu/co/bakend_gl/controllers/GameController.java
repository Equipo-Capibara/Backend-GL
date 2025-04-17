package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;

import java.util.ArrayList;


@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final RoomService roomService;

    public GameController(GameService gameService, PlayerService playerService, RoomService roomService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.roomService = roomService;
    }

    @PostMapping("/move")
    public ResponseEntity<Board> movePlayer(@RequestParam String roomCode, @RequestParam String playerId, @RequestParam String direction) {
        gameService.movePlayer(roomCode, playerId, direction);
        return ResponseEntity.ok(gameService.getBoard(roomCode));
    }

    @GetMapping("/state")
    @ResponseBody
    public Board getGameState(@RequestParam String roomCode) {
        return gameService.getBoard(roomCode);
    }

    @PostMapping("/createPlayer")
    public ResponseEntity<Player> createPlayer(@RequestParam String name) {
        Player newPlayer = playerService.createPlayer(name);
        System.out.println(newPlayer.getName());
        System.out.println(newPlayer.getId());
        return ResponseEntity.ok(newPlayer);
    }

    @GetMapping("/switch-level")
    public void switchLevel(@RequestParam int level) {
        gameService.switchLevel(level);
    }

    // Nuevo endpoint para construir bloque
    @PostMapping("/build")
    public ResponseEntity<Board> buildBlock(@RequestParam String roomCode, @RequestParam String playerId) {
        gameService.buildBlocks(roomCode, playerId);
        return ResponseEntity.ok(gameService.getBoard(roomCode));
    }

    @PostMapping("/destroy")
    public ResponseEntity<Board> destroyBlock(@RequestParam String roomCode, @RequestParam String playerId) {
        gameService.destroyBlock(roomCode, playerId);
        return ResponseEntity.ok(gameService.getBoard(roomCode));
    }

    @PostMapping("/start")
    public ResponseEntity<String> startGame(@RequestParam String roomCode, @RequestParam String requesterId) {
        boolean started = roomService.startGame(roomCode, requesterId);

        if (started) {
            // Obtener los jugadores actuales de la sala
            Room room = roomService.getRoom(roomCode);
            gameService.initializeGameForRoom(roomCode, new ArrayList<>(room.getPlayers().values()));

            return ResponseEntity.ok("Game started");
        } else {
            return ResponseEntity
                    .badRequest()
                    .body("No se puede iniciar el juego");
        }
    }


}
