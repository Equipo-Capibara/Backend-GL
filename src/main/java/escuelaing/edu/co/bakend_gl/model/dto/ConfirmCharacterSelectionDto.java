package escuelaing.edu.co.bakend_gl.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmCharacterSelectionDto {

    private String roomCode;
    private String playerId;

}
