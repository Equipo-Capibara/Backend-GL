package escuelaing.edu.co.bakend_gl.model.keys;

import escuelaing.edu.co.bakend_gl.model.characters.Brisa;
import escuelaing.edu.co.bakend_gl.model.characters.Character;

public class KeyBrisa extends Key {
    public KeyBrisa(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean canBePickedBy(Character character) {
        return character instanceof Brisa;
    }
}
