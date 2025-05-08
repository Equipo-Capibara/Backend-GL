package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.model.dto.ActionPlayerDto;
import escuelaing.edu.co.bakend_gl.model.dto.BoardStateDto;
import escuelaing.edu.co.bakend_gl.services.BoardService;
import escuelaing.edu.co.bakend_gl.services.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService gameService;
    private final BoardService boardService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Endpoint para mover un jugador
     */
    @MessageMapping("/game/{roomCode}/move")
    public void movePlayer(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        log.info("Recibida petición de movimiento en sala {}: jugador {} dirección {}",
            roomCode, actionPlayerDto.getPlayerId(), actionPlayerDto.getDirection());

        // Ejecutar movimiento
        gameService.movePlayer(roomCode, actionPlayerDto.getPlayerId(), actionPlayerDto.getDirection());

        // Obtener tablero actualizado
        Board board = gameService.getBoardByRoomCode(roomCode);

        if (board != null) {
            // Convertir a DTO para enviar solo los datos necesarios
            BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "move");

            // Enviar actualización a todos los clientes suscritos
            messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/state", boardState);

            // Actualizar en Redis con tiempo de expiración
            boardService.saveWithExpiration(board, 2, TimeUnit.HOURS);
        }
    }

    /**
     * Endpoint para construir bloques
     */
    @MessageMapping("/game/{roomCode}/build")
    public void buildBlock(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        log.info("Recibida petición para construir bloques en sala {}: jugador {}",
            roomCode, actionPlayerDto.getPlayerId());

        // Ejecutar construcción
        gameService.buildBlocks(roomCode, actionPlayerDto.getPlayerId());

        // Obtener tablero actualizado
        Board board = gameService.getBoardByRoomCode(roomCode);

        if (board != null) {
            // Convertir a DTO
            BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "build");

            // Enviar actualización
            messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/state", boardState);

            // Actualizar en Redis
            boardService.saveWithExpiration(board, 2, TimeUnit.HOURS);
        }
    }

    /**
     * Endpoint para destruir bloques
     */
    @MessageMapping("/game/{roomCode}/destroy")
    public void destroyBlock(@DestinationVariable String roomCode, @Payload ActionPlayerDto actionPlayerDto) {
        log.info("Recibida petición para destruir bloques en sala {}: jugador {}",
            roomCode, actionPlayerDto.getPlayerId());

        // Ejecutar destrucción
        gameService.destroyBlock(roomCode, actionPlayerDto.getPlayerId());

        // Obtener tablero actualizado
        Board board = gameService.getBoardByRoomCode(roomCode);

        if (board != null) {
            // Convertir a DTO
            BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "destroy");

            // Enviar actualización
            messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/state", boardState);

            // Actualizar en Redis
            boardService.saveWithExpiration(board, 2, TimeUnit.HOURS);
        }
    }

    /**
     * Endpoint para obtener el estado actual del tablero (útil para reconexiones)
     */
    @MessageMapping("/game/{roomCode}/get-state")
    public void getGameState(@DestinationVariable String roomCode) {
        log.info("Solicitando estado actual del tablero para sala: {}", roomCode);

        Board board = gameService.getBoardByRoomCode(roomCode);

        if (board != null) {
            // Convertir a DTO
            BoardStateDto boardState = BoardStateDto.fromBoard(board, roomCode, "state");

            // Enviar estado actual
            messagingTemplate.convertAndSend("/topic/game/" + roomCode + "/state", boardState);
        } else {
            log.warn("No se encontró tablero para la sala: {}", roomCode);
        }
    }
}
