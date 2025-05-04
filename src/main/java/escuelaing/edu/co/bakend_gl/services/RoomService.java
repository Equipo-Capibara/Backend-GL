package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.model.characters.CharacterType;
import escuelaing.edu.co.bakend_gl.repository.PlayerRepository;
import escuelaing.edu.co.bakend_gl.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;

    public Room createRoom(String hostId) {
        try {
            Room room = new Room(hostId);
            Player player = playerRepository.findById(hostId).orElseThrow();
            log.info("Jugador encontrado a la hora de crear una sala: {}", player);
            room.addPlayer(player);
            roomRepository.save(room);
            return room;
        } catch (NoSuchElementException e) {
            log.error("Jugador no encontrado con el id: {}", hostId);
            throw new RuntimeException(e);
        }
    }

    public boolean joinRoom(String roomCode, String playerId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            log.warn("Sala no encontrada con el codigo: {}", roomCode);
            return false;
        }

        synchronized (room) {
            if (!room.canJoin()) {
                log.warn("Sala {} no disponible para unirse", roomCode);
                return false;
            }

            if (room.getPlayers().containsKey(playerId)) {
                log.info("El jugador {} ya esta en la sala {}", playerId, roomCode);
                return false;
            }

            try {
                Player player = playerRepository.findById(playerId).get();
                room.addPlayer(player);
                log.info("Jugadores en la room {}: {}", roomCode, room.getPlayers());
                roomRepository.save(room);
                return true;
            } catch (NoSuchElementException e) {
                log.error("Jugador no encontrado con el id: {}", playerId);
                return false;
            }
        }
    }

    public boolean confirmCharacterSelection(String roomCode, String playerId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            log.warn("Sala no encontrada con el codigo: {}", roomCode);
            return false;
        }

        synchronized (room) {
            if (!room.confirmCharacterSelection(playerId)) {
                log.warn("Jugador {} ya seleccionado", playerId);
                return false;
            }

            roomRepository.save(room);
            return true;
        }
    }

    public boolean selectChatacter(String roomCode, String playerId, String characterId) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            log.warn("Sala no encontrada con el codigo: {}", roomCode);
            return false;
        }

        synchronized (room) {
            if (!room.getPlayers().containsKey(playerId)) {
                log.info("El jugador {} no esta en la s {}", playerId, roomCode);
                return false;
            }

            Player player = room.getPlayers().get(playerId);

            CharacterType type = CharacterType.mapNumberToCharacterType(characterId);
            if (type != null) {
                player.setCharacter(characterId);
                room.getPlayers().put(playerId, player);
                roomRepository.save(room);
            }
            return true;
        }
    }

    public boolean startGame(String roomCode) {
        Room room = roomRepository.findByCode(roomCode);
        if (room == null) {
            log.warn("Sala no encontrada con el codigo: {}", roomCode);
            return false;
        }

        log.info("Host de la sala: {} ¿Todos confirmaron?:  {}", room.getHostId(), room.allPlayersConfirmed());

        synchronized (room) {
            if (!room.allPlayersConfirmed()) {
                log.info("En la sala {} no han aceptado todos lo jugadores", roomCode);
                return false;
            }

            if (room.isGameStarted()) {
                log.info("El juego ya está iniciado.");
                return false;
            }

            room.setGameStarted(true);
            roomRepository.save(room);
            return true;
        }
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

