package escuelaing.edu.co.bakend_gl.model.characters;

public class Stone extends Character {
    public Stone(String id, int x, int y) {
        super(id, x, y, "stone");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}
