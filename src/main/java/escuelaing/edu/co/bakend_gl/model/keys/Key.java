package escuelaing.edu.co.bakend_gl.model.keys;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import escuelaing.edu.co.bakend_gl.model.characters.Character;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "keyType", visible = true)
@JsonSubTypes({
        @Type(value = KeyFlame.class, name = "keyflame"),
        @Type(value = KeyAqua.class, name = "keyaqua"),
        @Type(value = KeyStone.class, name = "keystone"),
        @Type(value = KeyBrisa.class, name = "keybrisa")
})
@Getter
@Setter
@AllArgsConstructor
public abstract class Key implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int x, y;
    protected String keyType; // Campo para guardar el tipo de llave

    public Key() {
        this.keyType = this.getClass().getSimpleName().toLowerCase();
    }

    public Key(int x, int y) {
        this.x = x;
        this.y = y;
        this.keyType = this.getClass().getSimpleName().toLowerCase();
    }

    public abstract boolean canBePickedBy(Character character);
}