package escuelaing.edu.co.bakend_gl.model.basicComponents;

public class RoomJoinRequest {

    private String roomCode;
    private String playerId;
    private String playerName;

    public RoomJoinRequest(String roomCode, String playerId, String playerName) {
        this.roomCode = roomCode;
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return "RoomJoinRequest{" +
                "roomCode='" + roomCode + '\'' +
                ", playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                '}';
    }

}
