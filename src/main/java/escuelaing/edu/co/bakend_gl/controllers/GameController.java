package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import org.springframework.context.annotation.Lazy;
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
    public void movePlayer(@RequestParam String playerId, @RequestParam String direction) {
        gameService.movePlayer(playerId, direction);
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


}