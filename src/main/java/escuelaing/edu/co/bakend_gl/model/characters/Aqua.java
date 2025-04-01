package escuelaing.edu.co.bakend_gl.model.characters;

public class Aqua extends Character {
    public Aqua(int x, int y) {
        super(x, y, "agua");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}
