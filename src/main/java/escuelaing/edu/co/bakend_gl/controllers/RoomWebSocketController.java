package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.model.dto.CharacterSelectionDto;
import escuelaing.edu.co.bakend_gl.model.dto.ConfirmCharacterSelectionDto;
import escuelaing.edu.co.bakend_gl.model.dto.JoinRoomDto;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final PlayerService playerService;

    @MessageMapping("/room/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId, @Payload JoinRoomDto joinRoomDto) {
        String username = joinRoomDto.getUsername();
        String playerId = joinRoomDto.getPlayerId();

        log.info("Usuario {} con id: {} ingresando a la sala: {}", username, playerId, roomId);

        Boolean joined = roomService.joinRoom(roomId, playerId);

        Map<String, Object> alert = Map.of("username", username, "playerId", playerId, "joined", joined);
        Map<String, Object> players = Map.of("players", roomService.getPlayersInRoom(roomId).values());

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/join-alert", alert);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/players",  players);
    }

    @MessageMapping("/confirmCharacterSelection")
    public void confirmCharacterSelection(@Payload ConfirmCharacterSelectionDto confirmCharacterSelectionDto) {
        String roomCode = confirmCharacterSelectionDto.getRoomCode();
        String playerId = confirmCharacterSelectionDto.getPlayerId();

        log.info("Recibiendo confirmación: {}", confirmCharacterSelectionDto);

        Boolean roomConfirm = roomService.confirmCharacterSelection(roomCode, playerId);

        log.info("Estado de confrimacion: {}", roomConfirm);

        Map<String, Object> players = Map.of("players", roomService.getPlayersInRoom(roomCode).values());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/confirm", players);
    }

    @MessageMapping("/removePlayer")
    public void removePlayer(@Payload Map<String, String> payload) {
        String roomCode = payload.get("roomCode");
        String playerId = payload.get("playerId");
        boolean removed = roomService.removePlayer(roomCode, playerId);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/player-removed",
                Map.of("playerId", playerId, "success", removed));
    }

    @MessageMapping("/reconnect/{roomId}")
    public void handleReconnection(@DestinationVariable String roomId, @Payload Map<String, String> payload) {
        String playerId = payload.get("playerId");
        Room room = roomService.getRoom(roomId);
        boolean isStillInRoom = room != null && room.getPlayers().containsKey(playerId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/reconnection",
                Map.of("playerId", playerId, "inRoom", isStillInRoom));
    }

    @MessageMapping("/room/{roomId}/character-select")
    public void handleCharacterSelect(@DestinationVariable String roomId, @Payload CharacterSelectionDto characterSelectionDto) {
        String playerId = characterSelectionDto.getPlayerId();
        String characterId = characterSelectionDto.getCharacter();
        log.info("El jugador {} tiene el personaje {}", playerId, characterId);

        Boolean isCharacterSelected = roomService.selectChatacter(roomId, playerId, characterId);

        log.info("Estado de la selecion de personaje: {}", isCharacterSelected);

        Map<String, Object> players = Map.of("players", roomService.getPlayersInRoom(roomId).values());

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/character-select", players);
    }

    @MessageMapping("/room/{roomId}/start")
    public void startGame(@DestinationVariable String roomId) {
        Boolean started = roomService.startGame(roomId);

        Map<String, Object> gameState = Map.of("players", roomService.getPlayersInRoom(roomId).values(), "status", "started", "roomId", roomId);
        Map<String, Object> state = Map.of("gameState", gameState);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/start", state);
    }

}
