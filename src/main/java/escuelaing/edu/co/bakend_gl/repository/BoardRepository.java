package escuelaing.edu.co.bakend_gl.repository;

import org.springframework.data.repository.CrudRepository;
import escuelaing.edu.co.bakend_gl.model.board.Board;

public interface BoardRepository extends CrudRepository<Board, String> {
    // Métodos adicionales si son necesarios
} 