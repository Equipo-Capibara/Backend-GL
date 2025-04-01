package escuelaing.edu.co.bakend_gl.model.characters;

public class Brisa extends Character {
    public Brisa(int x, int y) {
        super(x, y, "aire");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}