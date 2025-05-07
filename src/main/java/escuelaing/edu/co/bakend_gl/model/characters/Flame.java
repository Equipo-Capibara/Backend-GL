package escuelaing.edu.co.bakend_gl.model.characters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Flame extends Character {

    public Flame(int x, int y) {
        super(x, y, "Flame");
    }

    @Override
    public void useAbility() {
        log.info("Habilidad especial en mantenimiento");
    }

}