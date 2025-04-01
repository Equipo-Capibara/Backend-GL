package escuelaing.edu.co.bakend_gl.repository;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByCode(String code);
}
