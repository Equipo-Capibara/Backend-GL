package escuelaing.edu.co.bakend_gl.model.characters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Brisa extends Character {

    public Brisa(int x, int y) {
        super(x, y, "Brisa");
    }

    @Override
    public void useAbility() {
        log.info("Habilidad especial en mantenimiento");
    }

}