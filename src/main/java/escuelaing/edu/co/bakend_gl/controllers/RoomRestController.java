package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Room;
import escuelaing.edu.co.bakend_gl.model.dto.CreateRoomDto;
import escuelaing.edu.co.bakend_gl.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@ResponseBody
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomDto createRoomDto) {
        try {
            String hostId = createRoomDto.getHostId();
            log.info("Iniciando creacion de sala con el id de jugador: {}", hostId);
            Room room = roomService.createRoom(hostId);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
