package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.*;


@Controller
@RequestMapping("/api/room")
@ResponseBody
public class RoomController {

    private final RoomService roomService;
    private final PlayerService playerService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public RoomController(RoomService roomService, PlayerService playerService) {
        this.roomService = roomService;
        this.playerService = playerService;
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestBody Map<String, String> data) {
        String hostId = data.get("hostId");
        Room room = roomService.createRoom(hostId);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @MessageMapping("/room/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        String username = (String) payload.get("username");
        String playerId = (String) payload.get("playerId");

        System.out.println("Payload recibido: " + payload);

        Room room = roomService.getRoom(roomId);
        if (room != null && room.canJoin()) {
            Optional<Player> existingPlayer = room.getPlayers().values().stream()
                    .filter(p -> p.getId().equals(playerId))
                    .findFirst();

            Player player = playerService.getPlayerById(playerId).get();
            roomService.joinRoom(roomId, player);
            System.out.println(roomService.getPlayersInRoom(roomId));

            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/join-alert",
                    Map.of("username", username, "playerId", player.getId(), "joined", true));

            // CAMBIO AQUÍ: topic específico para lista de jugadores
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/players",
                    Map.of("players", roomService.getPlayersInRoom(roomId).values()));
        }
    }

    @MessageMapping("/confirmCharacterSelection")
    public void confirmCharacterSelection(@Payload Map<String, String> payload) {

        System.out.println("Recibiendo mensaje de confirmación de selección...");
        String roomCode = payload.get("roomCode");
        String playerId = payload.get("playerId");
        String characterId = payload.get("characterId");

        // Verifica que los valores que recibes sean correctos
        System.out.println("Recibiendo confirmación: roomCode=" + roomCode + ", playerId=" + playerId + ", characterId=" + characterId);

        Boolean change = roomService.confirmCharacterSelection(roomCode, playerId);
        System.out.println("Resultado de confirmación: " + change); // Verificar si el valor de 'change' es correcto

        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/confirm", payload);
    }

    @MessageMapping("/startGame")
    public void startGame(@Payload Map<String, String> payload) {
        String roomCode = payload.get("roomCode");
        boolean started = roomService.startGame(roomCode);
        messagingTemplate.convertAndSend("/topic/game/start/" + roomCode,
                Map.of("message", started ? "El juego ha comenzado" : "No se pudo iniciar el juego"));
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
    public void handleCharacterSelect(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");
        String newCharacter = String.valueOf(payload.get("character")); // ID del nuevo personaje

        Room room = roomService.getRoom(roomId);
        if (room != null && room.getPlayers().containsKey(playerId)) {
            Player player = room.getPlayers().get(playerId);
            player.setCharacter(newCharacter);
            room.getPlayers().put(playerId, player);
            roomService.saveRoom(room);

            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/character-select",
                    Map.of("players", room.getPlayers().values()));
        }
    }
}
