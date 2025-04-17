package escuelaing.edu.co.bakend_gl.model.characters;

public class Stone extends Character {
    public Stone(int x, int y) {
        super(x, y, "Stone");
    }

    @Override
    public void useAbility() {
        System.out.println("Habilidad especial en mantenimiento");
    }
}