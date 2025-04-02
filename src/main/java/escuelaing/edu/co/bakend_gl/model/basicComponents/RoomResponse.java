package escuelaing.edu.co.bakend_gl.model.basicComponents;

public class RoomResponse {
    private Room room;
    private String errorMessage;

    public RoomResponse(Room room, String errorMessage) {
        this.room = room;
        this.errorMessage = errorMessage;
    }

    // Getters y Setters
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
