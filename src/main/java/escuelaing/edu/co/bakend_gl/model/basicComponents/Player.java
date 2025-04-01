package escuelaing.edu.co.bakend_gl.model.basicComponents;

public class Player {

    private String id;
    private String name;
    private boolean characterSelected;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.characterSelected = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCharacterSelected() {
        return characterSelected;
    }

    public void setCharacterSelected(boolean characterSelected) {
        this.characterSelected = characterSelected;
    }

}
