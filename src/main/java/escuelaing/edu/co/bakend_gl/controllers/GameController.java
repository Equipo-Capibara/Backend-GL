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
    public ResponseEntity<Board> movePlayer(@RequestParam String direction, @RequestParam String playerId) {
        gameService.movePlayer(playerId, direction);
        return ResponseEntity.ok(gameService.getBoard());
    }

    @GetMapping("/state")
    @ResponseBody
    public Board getGameState() {
        return gameService.getBoard();
    }


    @PostMapping("/createBlock")
    public void createBlock(@RequestParam String playerId){
        gameService.createBlock(playerId);
    }

    @PostMapping("/destroyBlock")
    public void destroyBlock(@RequestParam String playerId){
        gameService.destroyBlock(playerId);
    }

    @GetMapping("/switch-level")
    public void switchLevel(@RequestParam int level) {
        gameService.switchLevel(level);
    }
}