package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.repository.PlayerRepository;
import escuelaing.edu.co.bakend_gl.repository.RoomRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    public RoomService(RoomRepository roomRepository, PlayerRepository playerRepository) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
    }

    // Crear una nueva sala
    public Room createRoom(String hostId) {
        Room room = new Room(hostId);
        room.addPlayer(playerRepository.findById(hostId).get());
        // Guardar la sala en MongoDB
        roomRepository.save(room);
        return room;
    }

    // Unirse a una sala existente
    public boolean joinRoom(String roomCode, Player player) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            return false;
        }

        if (room.canJoin()) {
            room.addPlayer(player);
            roomRepository.save(room);
            return true;
        }
        return false;
    }

    // Confirmar selección de personaje
    public boolean confirmCharacterSelection(String roomCode, String playerId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room != null && room.confirmCharacterSelection(playerId)) {
            roomRepository.save(room);
            return true;
        }
        return false;
    }

    // Iniciar el juego
    public boolean startGame(String roomCode, String requesterId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room != null && room.getHostId().equals(requesterId) && room.allPlayersConfirmed() && !room.isGameStarted()) {
            room.startGame();
            roomRepository.save(room);
            return true;
        }
        return false;
    }

    // Eliminar un jugador de la sala
    public boolean removePlayer(String roomCode, String playerId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room != null) {
            room.removePlayer(playerId);
            // Guardar cambios en la base de datos
            roomRepository.save(room);
            return true;
        }
        return false;
    }

    // Obtener una sala por su código
    public Room getRoom(String roomCode) {
        return roomRepository.findByCode(roomCode);
    }

    public Map<String, Player> getPlayersInRoom(String roomId) {
        Room room = getRoom(roomId); // Asumiendo que ya tienes este método
        return room.getPlayers();
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
}

