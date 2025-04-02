package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.repository.RoomRepository;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Crear una nueva sala
    public Room createRoom(String hostId) {
        Room room = new Room(hostId);
        // Guardar la sala en MongoDB
        roomRepository.save(room);
        return room;
    }

    // Unirse a una sala existente
    public boolean joinRoom(String roomCode, Player player) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            return false; // Sala no encontrada
        }

        if (room.canJoin()) {
            room.addPlayer(player);
            // Guardar cambios en la base de datos
            roomRepository.save(room);
            return true;
        }
        return false; // No se puede unir
    }

    // Confirmar selección de personaje
    public boolean confirmCharacterSelection(String roomCode, String playerId) {
        Room room = roomRepository.findByCode(roomCode);
        return room != null && room.confirmCharacterSelection(playerId);
    }

    // Iniciar el juego
    public boolean startGame(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room != null && room.allPlayersConfirmed()) {
            room.startGame();
            // Guardar cambios en la base de datos
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
}

