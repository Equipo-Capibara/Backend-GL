package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.basicComponents.Player;
import escuelaing.edu.co.bakend_gl.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(String name) {
        // Verificar que el nombre no sea nulo ni vacío
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        Player newPlayer = new Player(name);
        return playerRepository.save(newPlayer);
    }

    public Optional<Player> getPlayerById(String id) {
        return playerRepository.findById(id);
    }

}

