package escuelaing.edu.co.bakend_gl.model.characters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "element", visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Aqua.class, names = "Aqua"),
        @JsonSubTypes.Type(value = Flame.class, names = "Flame"),
        @JsonSubTypes.Type(value = Stone.class, names = "Stone"),
        @JsonSubTypes.Type(value = Brisa.class, names = "Brisa"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Character implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int x;
    protected int y;
    protected String element;
    protected String playerId;  // Identificador del jugador que controla este personaje
    protected String direction = "s"; // w - arriba, s - abajo, a - izq, d - der

    protected Character(int x, int y, String element) {
        this.x = x;
        this.y = y;
        this.element = element;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void useAbility();
}