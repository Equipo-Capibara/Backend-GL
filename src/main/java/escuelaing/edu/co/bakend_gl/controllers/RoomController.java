package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.model.characters.CharacterType;
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

        Boolean roomConfirm = roomService.confirmCharacterSelection(roomCode, playerId);
        System.out.println("estado" + roomConfirm);

        Collection<Player> players = roomService.getPlayersInRoom(roomCode).values();
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
    public void handleCharacterSelect(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        String playerId = (String) payload.get("playerId");
        String characterIdStr = String.valueOf(payload.get("character")); // ID del nuevo personaje
        Room room = roomService.getRoom(roomId);

        if (room != null && room.getPlayers().containsKey(playerId)) {
            Player player = room.getPlayers().get(playerId);

            CharacterType type = mapNumberToCharacterType(characterIdStr);
            if(type != null){
                player.setCharacter(characterIdStr);
                room.getPlayers().put(playerId, player);
                roomService.saveRoom(room);
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/character-select",
                        Map.of("players", room.getPlayers().values()));
            }
        }
    }

    @MessageMapping("/room/{roomId}/start")
    public void startGame(String messageBody, @org.springframework.messaging.handler.annotation.DestinationVariable String roomId) {
        try {
            // Suponiendo que el mensaje contiene el roomId y los jugadores seleccionados (ya registrados en el Room)
            Room room = roomService.getRoom(roomId);
            if (room != null && roomService.startGame(roomId, room.getHostId())) {

                // Crear un estado inicial del juego (ejemplo simplificado)
                Map<String, Object> gameState = Map.of(
                        "players", room.getPlayers(),
                        "status", "started",
                        "roomId", roomId
                );

                // Enviar mensaje a todos los suscritos al tópico de esta sala
                messagingTemplate.convertAndSend(
                        "/topic/room/" + roomId + "/start",
                        Map.of("gameState", gameState)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CharacterType mapNumberToCharacterType(String characterIdStr) {
        switch (characterIdStr) {
            case "1": return CharacterType.FLAME;
            case "2": return CharacterType.AQUA;
            case "3": return CharacterType.BRISA;
            case "4": return CharacterType.STONE;
            default: return null; // O puedes lanzar una excepción si prefieres
        }
    }

}
