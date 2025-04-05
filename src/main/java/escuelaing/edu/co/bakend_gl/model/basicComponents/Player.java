package escuelaing.edu.co.bakend_gl.model.basicComponents;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "players")
public class Player {

    @Id
    private String id;
    private String name;
    private String character;
    private boolean characterSelected;

    public Player() {}

    public Player( String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.character = "1";
        this.characterSelected = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCharacter() { return character; }
    public boolean isCharacterSelected() { return characterSelected; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCharacter(String character) { this.character = character; }
    public void setCharacterSelected(boolean selected) { this.characterSelected = selected; }
}
