package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.dto.ActionPlayerDto;
import escuelaing.edu.co.bakend_gl.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService gameService;
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/game/{roomCode}/move")
    public void movePlayer(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        gameService.movePlayer(roomCode, actionPlayerDto.getPlayerId(), actionPlayerDto.getDirection());
        Board board = gameService.getBoard(roomCode);
        messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/move" + board);
    }

    @MessageMapping("/game/{roomCode}/build")
    public void buildBlock(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        gameService.buildBlocks(roomCode, actionPlayerDto.getPlayerId());
        Board board = gameService.getBoard(roomCode);
        messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/build" + board);
    }

    @MessageMapping("/game/{roomCode}/destroy")
    public void destroyBlock(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        gameService.destroyBlock(roomCode, actionPlayerDto.getPlayerId());
        Board board = gameService.getBoard(roomCode);
        messagingTemplate.convertAndSend("/topic/game" + roomCode + "/destroy" + board);
    }
}
