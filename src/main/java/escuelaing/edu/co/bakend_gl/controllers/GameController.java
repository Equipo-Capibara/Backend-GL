package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/state")
    @ResponseBody
    public Board getGameState(@RequestParam String roomCode) {
        return gameService.getBoard(roomCode);
    }

    @GetMapping("/switch-level")
    public void switchLevel(@RequestParam int level) {
        gameService.switchLevel(level);
    }

}
