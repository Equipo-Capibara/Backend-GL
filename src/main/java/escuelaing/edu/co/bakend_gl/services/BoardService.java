package escuelaing.edu.co.bakend_gl.services;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final RedisTemplate<String, Board> boardRedisTemplate;
    private final String BOARD_KEY_PREFIX = "board:";

    @Autowired
    public BoardService(BoardRepository boardRepository, RedisTemplate<String, Board> boardRedisTemplate) {
        this.boardRepository = boardRepository;
        this.boardRedisTemplate = boardRedisTemplate;
    }

    /**
     * Crea un nuevo tablero y lo almacena en Redis
     *
     * @param width  Ancho del tablero
     * @param height Alto del tablero
     * @return El tablero creado
     */
    public Board createBoard(int width, int height) {
        Board board = new Board(width, height);

        // Generar un código de sala de 6 caracteres que será usado como ID
        String roomCode = generateRoomCode();
        board.setId(roomCode);

        log.info("Creando nuevo tablero con roomCode: {}", roomCode);

        // Guardar en el repositorio
        return boardRepository.save(board);
    }

    /**
     * Genera un código de sala único de 6 caracteres
     */
    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    /**
     * Busca un tablero por su ID
     *
     * @param id ID del tablero (roomCode)
     * @return Tablero encontrado o vacío si no existe
     */
    public Optional<Board> getBoardById(String id) {
        return boardRepository.findById(id);
    }

    /**
     * Actualiza un tablero existente
     *
     * @param board Tablero a actualizar
     * @return Tablero actualizado
     */
    public Board updateBoard(Board board) {
        try {
            // También actualizar en Redis para mantener la consistencia
            saveWithExpiration(board, 2, TimeUnit.HOURS);
            return boardRepository.save(board);
        } catch (SerializationException e) {
            log.error("Error al serializar el tablero: {}", e.getMessage());
            // Intentar borrar el registro de Redis que está causando problemas
            String key = BOARD_KEY_PREFIX + board.getId();
            boardRedisTemplate.delete(key);
            log.info("Se eliminó la clave {} de Redis para solucionar problemas de serialización", key);
            // Intentar guardar nuevamente
            saveWithExpiration(board, 2, TimeUnit.HOURS);
            return boardRepository.save(board);
        }
    }

    /**
     * Elimina un tablero
     *
     * @param id ID del tablero a eliminar
     */
    public void deleteBoard(String id) {
        // Eliminar también de Redis para mantener consistencia
        String key = BOARD_KEY_PREFIX + id;
        boardRedisTemplate.delete(key);
        boardRepository.deleteById(id);
    }

    /**
     * Obtiene todos los tableros
     *
     * @return Lista de tableros
     */
    public List<Board> getAllBoards() {
        return StreamSupport
                .stream(boardRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Guarda un tablero en Redis con un tiempo de expiración
     *
     * @param board          Tablero a guardar
     * @param expirationTime Tiempo de expiración
     * @param timeUnit       Unidad de tiempo
     */
    public void saveWithExpiration(Board board, long expirationTime, TimeUnit timeUnit) {
        try {
            String key = BOARD_KEY_PREFIX + board.getId();
            boardRedisTemplate.opsForValue().set(key, board, expirationTime, timeUnit);
            log.info("Tablero guardado en Redis con clave: {}", key);
        } catch (SerializationException e) {
            log.error("Error al guardar en Redis: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un tablero de Redis usando el roomCode
     *
     * @param roomCode Código de la sala (ID del tablero)
     * @return Tablero encontrado o null si no existe
     */
    public Board getBoardFromRedis(String roomCode) {
        try {
            String key = BOARD_KEY_PREFIX + roomCode;
            Board board = boardRedisTemplate.opsForValue().get(key);
            if (board == null) {
                log.warn("No se encontró tablero en Redis con clave: {}", key);
            }
            return board;
        } catch (SerializationException e) {
            log.error("Error al deserializar el tablero de Redis: {} - {}", roomCode, e.getMessage());
            // Limpiar el objeto inválido de Redis
            boardRedisTemplate.delete(BOARD_KEY_PREFIX + roomCode);
            log.info("Se eliminó el objeto inválido con clave: {}", BOARD_KEY_PREFIX + roomCode);
            return null;
        }
    }

    /**
     * Limpia todos los tableros en Redis - útil para resolver problemas de serialización
     */
    public void clearRedisBoards() {
        // Encontrar todas las claves con nuestro prefijo
        for (String key : boardRedisTemplate.keys(BOARD_KEY_PREFIX + "*")) {
            boardRedisTemplate.delete(key);
            log.info("Eliminado tablero de Redis: {}", key);
        }
        log.info("Se han limpiado todos los tableros de Redis");
    }
} 