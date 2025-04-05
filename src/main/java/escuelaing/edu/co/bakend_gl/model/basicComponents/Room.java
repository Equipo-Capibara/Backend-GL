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
    private Map<String, Player> players = new HashMap<>();

    public Room() {}

    public Room(String hostId) {
        this.hostId = hostId;
        this.code = generateRoomCode();
        this.gameStarted = false;
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public boolean canJoin() {
        return !gameStarted && players.size() < 4;
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public boolean confirmCharacterSelection(String playerId) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setCharacterSelected(true);
            return true;
        }
        return false;
    }

    public boolean allPlayersConfirmed() {
        return players.values().stream().allMatch(Player::isCharacterSelected);
    }

    public void startGame() {
        gameStarted = true;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
}
