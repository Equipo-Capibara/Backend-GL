package escuelaing.edu.co.bakend_gl.model.basic_components;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;
    private String hostId;
    private String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    private boolean gameStarted = false;
    private Map<String, Player> players = new ConcurrentHashMap<>();

    public Room(String hostId) {
        this.hostId = hostId;
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
            synchronized (player) {
                player.setCharacterSelected(true);
            }
            return true;
        }
        return false;
    }

    public boolean allPlayersConfirmed() {
        return players.values().stream().allMatch(Player::isCharacterSelected);
    }

}
