package escuelaing.edu.co.bakend_gl.model.characters;

public class Flame extends Character {
    public Flame(String id, int x, int y) {
        super(id, x, y, "fire");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}