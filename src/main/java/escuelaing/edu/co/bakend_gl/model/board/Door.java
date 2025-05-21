package escuelaing.edu.co.bakend_gl.model.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Door implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private boolean isLocked;

    public void unlock() {
        this.isLocked = false;
    }

}
