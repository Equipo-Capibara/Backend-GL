package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController( GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/move")
    public void movePlayer(@RequestParam String direction) {
        gameService.movePlayer(direction);
    }

    @GetMapping("/state")
    @ResponseBody
    public Board getGameState() {
        return gameService.getBoard();
    }
}