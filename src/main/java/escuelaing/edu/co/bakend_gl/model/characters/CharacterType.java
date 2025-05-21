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

    public static CharacterType mapNumberToCharacterType(String characterIdStr) {
        return switch (characterIdStr) {
            case "1" -> CharacterType.FLAME;
            case "2" -> CharacterType.AQUA;
            case "3" -> CharacterType.BRISA;
            case "4" -> CharacterType.STONE;
            default -> null; // O puedes lanzar una excepción si prefieres
        };
    }

}
