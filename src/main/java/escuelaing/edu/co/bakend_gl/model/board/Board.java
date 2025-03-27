package escuelaing.edu.co.bakend_gl.model.board;

import escuelaing.edu.co.bakend_gl.model.characters.Character;
import escuelaing.edu.co.bakend_gl.model.blocks.Block;
import java.util.List;
public class Board {

    private int width;
    private int height;
    private List<Block> blocks;
    private Character player;

    public Board(int width, int height, List<Block> blocks, Character player) {
        this.width = width;
        this.height = height;
        this.blocks = blocks;
        this.player = player;
    }

    public Character getPlayer() {
        return player;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public boolean isMoveValid(int x, int y) {
        return blocks.stream().noneMatch(block -> block.getX() == x && block.getY() == y);
    }

    public void movePlayer(int newX, int newY) {
        if (isMoveValid(newX, newY)) {
            player.setPosition(newX, newY);
        }
    }

}
