package escuelaing.edu.co.bakend_gl.model.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true
)
@JsonSubTypes({
    @Type(value = BlockAir.class, name = "Air"),
    @Type(value = BlockFire.class, name = "Fire"),
    @Type(value = BlockWater.class, name = "Water"),
    @Type(value = BlockEarth.class, name = "Earth"),
    @Type(value = BlockIron.class, name = "Iron")
})
@Getter
@Setter
public abstract class Block implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int x;
    protected int y;
    protected String type;
    protected boolean destructible;
    protected String allowedCharacter; // Qué personaje puede destruirlo

    public Block(int x, int y, String type, boolean destructible, String allowedCharacter) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.destructible = destructible;
        this.allowedCharacter = allowedCharacter;
    }

}