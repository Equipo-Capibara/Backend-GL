package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Crear una nueva sala
    @MessageMapping("/createRoom")
    @SendTo("/topic/room")
    public ResponseEntity<Room> createRoom(String hostId) {
        Room room = roomService.createRoom(hostId);
        return new ResponseEntity<>(room, HttpStatus.CREATED); // Respuesta con código 201 (creado)
    }

    // Unirse a una sala
    @MessageMapping("/joinRoom")
    @SendTo("/topic/room/{roomCode}")
    public ResponseEntity<Object> joinRoom(String roomCode, Player player) {
        boolean joined = roomService.joinRoom(roomCode, player);
        if (joined) {
            return new ResponseEntity<>(roomService.getRoom(roomCode), HttpStatus.OK); // Respuesta con código 200 (OK)
        } else {
            return new ResponseEntity<>("No se pudo unir a la sala", HttpStatus.BAD_REQUEST); // Respuesta con código 400 (Bad Request)
        }
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