package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.dto.BoardStateDto;
import escuelaing.edu.co.bakend_gl.services.GameService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType; // nuevo
import org.springframework.http.ResponseEntity; // nuevo
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/{roomCode}/state")
    public ResponseEntity<Board> getGameState(@PathVariable String roomCode) {
        Board board = gameService.getBoard(roomCode);
        return ResponseEntity.ok(board);
    }

    @GetMapping("/switch-level")
    public void switchLevel(@RequestParam int level) {
        gameService.switchLevel(level);
    }

}
