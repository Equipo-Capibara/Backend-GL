package escuelaing.edu.co.bakend_gl.model.characters;

public class Brisa extends Character {
    public Brisa(String id, int x, int y) {
        super(id, x, y, "aire");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}