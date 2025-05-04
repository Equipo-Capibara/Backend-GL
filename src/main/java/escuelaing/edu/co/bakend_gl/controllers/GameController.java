package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import escuelaing.edu.co.bakend_gl.services.RoomService;


@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final RoomService roomService;

    // FIXME: websocket
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


}
