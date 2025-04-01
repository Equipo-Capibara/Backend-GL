package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.model.basicComponents.RoomJoinRequest;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public Room createRoom(@RequestParam String hostId) {
        return roomService.createRoom(hostId);
    }

    @MessageMapping("/join")
    public void joinRoom(@RequestBody Player player, @RequestParam String roomCode) {
        roomService.joinRoom(roomCode, player);
    }

    @MessageMapping("/confirmSelection")
    public void confirmSelection(@RequestParam String roomCode, @RequestParam String playerId) {
        roomService.confirmCharacterSelection(roomCode, playerId);
    }

    @MessageMapping("/startGame")
    public void startGame(@RequestParam String roomCode) {
        roomService.startGame(roomCode);
    }

    @MessageMapping("/expel")
    public void expelPlayer(@RequestParam String roomCode, @RequestParam String playerId) {
        roomService.removePlayer(roomCode, playerId);
    }

    @GetMapping("/{roomCode}")
    public Room getRoom(@PathVariable String roomCode) {
        return roomService.getRoom(roomCode);
    }

}
