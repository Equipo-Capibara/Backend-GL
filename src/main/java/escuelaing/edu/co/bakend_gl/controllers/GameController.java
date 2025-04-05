package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController( GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public ResponseEntity<Board> movePlayer(@RequestParam String direction) {
        gameService.movePlayer(direction);
        return ResponseEntity.ok(gameService.getBoard());
    }


    @GetMapping("/state")
    @ResponseBody
    public Board getGameState() {
        return gameService.getBoard();
    }

    @GetMapping("/switch-level")
    public void switchLevel(@RequestParam int level) {
        gameService.switchLevel(level);
    }

}