package escuelaing.edu.co.bakend_gl.model.characters;

public class Aqua extends Character {
    public Aqua(String id, int x, int y) {
        super(id, x, y, "agua");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}
