package escuelaing.edu.co.bakend_gl.model.basicComponents;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "rooms")
public class Room {

    @Id
    private String id;
    private String code;
    private String hostId;
    private boolean gameStarted;
    private Map<String, Player> players = new HashMap<>();  // Usar un Map en lugar de un Set

    public Room() {}

    public Room(String hostId) {
        this.hostId = hostId;
        this.code = generateRoomCode();
        this.gameStarted = false;
    }

    public String getCode() {
        return code;
    }

    public boolean canJoin() {
        return !gameStarted && players.size() < 4;
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);  // Usamos put en lugar de add
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public Collection<Player> getPlayers() {
        return players.values();  // Retorna los jugadores como una colección
    }

    public boolean confirmCharacterSelection(String playerId) {
        Player player = players.get(playerId);  // Usamos get para acceder al jugador por su playerId
        if (player != null) {
            player.setCharacterSelected(true);  // Asumiendo que Player tiene un campo `characterSelected`
            return true;
        }
        return false;
    }

    // Iniciar la partida si todos los jugadores confirmaron
    public boolean allPlayersConfirmed() {
        return players.values().stream().allMatch(Player::isCharacterSelected);  // Verifica si todos los jugadores han seleccionado personaje
    }

    // Iniciar el juego
    public void startGame() {
        gameStarted = true;
    }

    // Eliminar jugador de la sala
    public void removePlayer(String playerId) {
        players.remove(playerId);
    }
}
