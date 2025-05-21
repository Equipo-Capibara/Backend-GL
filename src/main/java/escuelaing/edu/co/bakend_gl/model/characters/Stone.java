package escuelaing.edu.co.bakend_gl.model.characters;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Stone extends Character {

    public Stone() {super(0,0, "Stone");}

    public Stone(int x, int y) {
        super(x, y, "Stone");
    }

    @Override
    public void useAbility() {
        log.info("Habilidad especial en mantenimiento");
    }

}