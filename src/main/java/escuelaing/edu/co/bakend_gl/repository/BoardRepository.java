package escuelaing.edu.co.bakend_gl.repository;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<Board, String> {

} 