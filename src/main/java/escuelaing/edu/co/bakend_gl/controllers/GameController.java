package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;

    public GameController( GameService gameService, PlayerService playerService) {

        this.gameService = gameService;
        this.playerService = playerService;
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

}