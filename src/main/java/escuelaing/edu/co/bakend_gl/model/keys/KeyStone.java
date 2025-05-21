package escuelaing.edu.co.bakend_gl.model.keys;

import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.characters.Stone;

public class KeyStone extends Key {

    public KeyStone() {super(0,0);}

    public KeyStone(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean canBePickedBy(Character character) {
        return character instanceof Stone;
    }
}
