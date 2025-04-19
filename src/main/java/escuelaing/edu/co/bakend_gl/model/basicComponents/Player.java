package escuelaing.edu.co.bakend_gl.model.basicComponents;

import escuelaing.edu.co.bakend_gl.model.characters.CharacterType;
import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "players")
public class Player {

    @Id
    private String id;
    private String name;
    private CharacterType character;
    private boolean characterSelected;
    private String idFront;

    public Player() {}

    // idFront
    // 1 - Flame
    // 2 - Aqua
    // 3 - Brisa
    // 4 - Stone

    public Player( String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.character = CharacterType.FLAME;// Por defecto dejamos a Flame
        this.idFront = "1";
        this.characterSelected = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public CharacterType getCharacter() { return character; }
    public boolean isCharacterSelected() { return characterSelected; }
    public String getIdFront() { return idFront; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    public void setCharacter(CharacterType character) {
        this.character = character;
        // Sincronizamos automáticamente el idFront
        switch (character) {
            case FLAME -> this.idFront = "1";
            case AQUA -> this.idFront = "2";
            case BRISA -> this.idFront = "3";
            case STONE -> this.idFront = "4";
            default -> this.idFront = "0";
        }
    }
    public void setCharacterSelected(boolean selected) { this.characterSelected = selected; }
}
