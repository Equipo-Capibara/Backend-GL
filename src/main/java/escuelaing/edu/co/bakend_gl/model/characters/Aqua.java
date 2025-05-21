package escuelaing.edu.co.bakend_gl.model.characters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Aqua extends Character {

    public Aqua() {super(0,0, "Aqua");}

    public Aqua(int x, int y) {
        super(x, y, "Aqua");
    }

    @Override
    public void useAbility() {
        log.info("Habilidad especial en mantenimiento");
    }

}