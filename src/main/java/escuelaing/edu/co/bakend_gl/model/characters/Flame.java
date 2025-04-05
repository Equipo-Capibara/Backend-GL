package escuelaing.edu.co.bakend_gl.model.characters;

public class Flame extends Character {
    public Flame(int x, int y) {
        super(x, y, "fire");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}