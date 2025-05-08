package escuelaing.edu.co.bakend_gl.model.keys;

import escuelaing.edu.co.bakend_gl.model.characters.Aqua;
import escuelaing.edu.co.bakend_gl.model.characters.Character;

public class KeyAqua extends Key {
    public KeyAqua(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean canBePickedBy(Character character) {
        return character instanceof Aqua;
    }
}
