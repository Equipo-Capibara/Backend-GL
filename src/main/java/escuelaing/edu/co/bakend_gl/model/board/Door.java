package escuelaing.edu.co.bakend_gl.model.board;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
