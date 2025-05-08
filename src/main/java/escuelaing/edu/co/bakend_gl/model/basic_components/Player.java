package escuelaing.edu.co.bakend_gl.model.basic_components;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "players")
public class Player {
    /**
     * Create an entity of a player
     * 1 - Flame
     * 2 - Aqua
     * 3 - Brisa
     * 4 - Stone
     */

    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String character = "1";
    private boolean characterSelected = false;

    public Player(String name) {
        this.name = name;
    }

}
