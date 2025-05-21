package escuelaing.edu.co.bakend_gl.repository;

import escuelaing.edu.co.bakend_gl.model.basic_components.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerRepository extends MongoRepository<Player, String> {


    List<Object> findByName(String hostId);
}
