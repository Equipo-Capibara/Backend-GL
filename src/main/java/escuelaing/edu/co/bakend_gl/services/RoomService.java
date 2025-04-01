package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new HashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public RoomService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public Room createRoom(String hostId) {
        Room room = new Room(hostId);
        rooms.put(room.getCode(), room);
        return room;
    }

    public Room getRoom(String roomCode) {
        return rooms.get(roomCode);
    }

    public void joinRoom(String roomCode, Player player) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            messagingTemplate.convertAndSend("/topic/errors", "Código de sala inválido");
            return;
        }

        if (!room.canJoin()) {
            messagingTemplate.convertAndSend("/topic/errors", "No puedes unirte, la sala está llena o en partida.");
            return;
        }

        room.addPlayer(player);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, room.getPlayers());
    }

    public void confirmCharacterSelection(String roomCode, String playerId) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            messagingTemplate.convertAndSend("/topic/errors", "La sala no existe.");
            return;
        }

        if (!room.confirmCharacterSelection(playerId)) {
            messagingTemplate.convertAndSend("/topic/errors", "No se pudo confirmar la selección del personaje.");
            return;
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomCode, room.getPlayers());
    }

    public void startGame(String roomCode) {
        Room room = rooms.get(roomCode);
        if (room == null || !room.allPlayersConfirmed()) {
            messagingTemplate.convertAndSend("/topic/errors", "No se puede iniciar la partida.");
            return;
        }

        room.startGame();
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/start", "¡La partida ha comenzado!");
    }

    public void removePlayer(String roomCode, String playerId) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            messagingTemplate.convertAndSend("/topic/errors", "La sala no existe.");
            return;
        }

        room.removePlayer(playerId);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, room.getPlayers());
        messagingTemplate.convertAndSend("/topic/player/" + playerId + "/expelled", "Has sido expulsado de la sala.");
    }

}
