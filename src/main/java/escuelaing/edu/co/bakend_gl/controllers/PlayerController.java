package escuelaing.edu.co.bakend_gl.controllers;

import escuelaing.edu.co.bakend_gl.model.basic_components.Player;
import escuelaing.edu.co.bakend_gl.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestParam String name) {
        try {
            Player newPlayer = playerService.createPlayer(name);
            log.info(
                    "Se ha creado el jugador {} con id {} en el endpoint /createPlayer de GameController",
                    newPlayer.getName(),
                    newPlayer.getId()
            );
            return ResponseEntity.ok(newPlayer);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear el jugador: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
