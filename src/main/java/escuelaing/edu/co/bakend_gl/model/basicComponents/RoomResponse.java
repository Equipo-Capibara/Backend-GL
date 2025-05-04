package escuelaing.edu.co.bakend_gl.model.basicComponents;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomResponse {
    private Room room;
    private String errorMessage;
}
