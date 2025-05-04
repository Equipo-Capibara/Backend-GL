package escuelaing.edu.co.bakend_gl.repository;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {
    Room findByCode(String code);
}
