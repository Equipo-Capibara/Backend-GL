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

    public Player() {}

    // 1 - Flame
    // 2 - Aqua
    // 3 - Brisa
    // 4 - Stone

    public Player( String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.character = CharacterType.FLAME; // Por defecto dejamos a Flame
        this.characterSelected = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public CharacterType getCharacter() { return character; }
    public boolean isCharacterSelected() { return characterSelected; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCharacter(CharacterType character) { this.character = character; }
    public void setCharacterSelected(boolean selected) { this.characterSelected = selected; }
}
