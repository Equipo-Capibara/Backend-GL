package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestBody String hostId) {
        Room room = roomService.createRoom(hostId);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    // Unirse a una sala
    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/topic/room/{roomId}/join-alert")
    public Map<String, String> joinRoom(@DestinationVariable String roomId, @Payload Map<String, String> payload) {
        System.out.println("📩 Nueva unión en la sala " + roomId);

        Map<String, String> message = new HashMap<>();
        message.put("username", payload.getOrDefault("username", "Jugador"));
        message.put("avatarUrl", payload.getOrDefault("avatarUrl", "default-avatar.png")); // Imagen por defecto

        return message;
    }

    // Confirmar selección de personaje
    @MessageMapping("/confirmCharacterSelection")
    @SendTo("/topic/room/{roomCode}")
    public ResponseEntity<Object> confirmCharacterSelection(String roomCode, String playerId) {
        boolean confirmed = roomService.confirmCharacterSelection(roomCode, playerId);
        if (confirmed) {
            return new ResponseEntity<>("Selección de personaje confirmada", HttpStatus.OK); // Respuesta con código 200 (OK)
        } else {
            return new ResponseEntity<>("Selección de personaje no confirmada", HttpStatus.BAD_REQUEST); // Respuesta con código 400 (Bad Request)
        }
    }

    // Iniciar el juego
    @MessageMapping("/startGame")
    @SendTo("/topic/game/start/{roomCode}")
    public ResponseEntity<String> startGame(String roomCode) {
        boolean started = roomService.startGame(roomCode);
        if (started) {
            return new ResponseEntity<>("El juego ha comenzado", HttpStatus.OK); // Respuesta con código 200 (OK)
        } else {
            return new ResponseEntity<>("No se pudo iniciar el juego", HttpStatus.BAD_REQUEST); // Respuesta con código 400 (Bad Request)
        }
    }

    // Eliminar un jugador
    @MessageMapping("/removePlayer")
    @SendTo("/topic/room/{roomCode}")
    public ResponseEntity<Object> removePlayer(String roomCode, String playerId) {
        boolean removed = roomService.removePlayer(roomCode, playerId);
        if (removed) {
            return new ResponseEntity<>("Jugador eliminado", HttpStatus.OK); // Respuesta con código 200 (OK)
        } else {
            return new ResponseEntity<>("No se pudo eliminar al jugador", HttpStatus.BAD_REQUEST); // Respuesta con código 400 (Bad Request)
        }
    }
}