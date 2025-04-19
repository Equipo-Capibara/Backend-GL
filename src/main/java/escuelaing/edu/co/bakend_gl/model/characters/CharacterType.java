package escuelaing.edu.co.bakend_gl.model.characters;

public enum CharacterType {
    FLAME("1"),
    AQUA("2"),
    BRISA("3"),
    STONE("4");

    private final String id;

    CharacterType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
