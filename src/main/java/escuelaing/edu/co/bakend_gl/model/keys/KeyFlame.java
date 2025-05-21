package escuelaing.edu.co.bakend_gl.model.keys;

import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.Flame;

public class KeyFlame extends Key {

    public KeyFlame() {super(0,0);}

    public KeyFlame(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean canBePickedBy(Character character) {
        return character instanceof Flame;
    }
}
