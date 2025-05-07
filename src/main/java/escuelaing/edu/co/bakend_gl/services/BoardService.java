package escuelaing.edu.co.bakend_gl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
     * @param width Ancho del tablero
     * @param height Alto del tablero
     * @return El tablero creado
     */
    public Board createBoard(int width, int height) {
        Board board = new Board(width, height);
        String boardId = UUID.randomUUID().toString();
        board.setId(boardId);
        
        // Guardar en el repositorio
        return boardRepository.save(board);
    }
    
    /**
     * Busca un tablero por su ID
     * @param id ID del tablero
     * @return Tablero encontrado o vacío si no existe
     */
    public Optional<Board> getBoardById(String id) {
        return boardRepository.findById(id);
    }
    
    /**
     * Actualiza un tablero existente
     * @param board Tablero a actualizar
     * @return Tablero actualizado
     */
    public Board updateBoard(Board board) {
        return boardRepository.save(board);
    }
    
    /**
     * Elimina un tablero
     * @param id ID del tablero a eliminar
     */
    public void deleteBoard(String id) {
        boardRepository.deleteById(id);
    }
    
    /**
     * Obtiene todos los tableros
     * @return Lista de tableros
     */
    public List<Board> getAllBoards() {
        return StreamSupport
                .stream(boardRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
    
    /**
     * Guarda un tablero en Redis con un tiempo de expiración
     * @param board Tablero a guardar
     * @param expirationTime Tiempo de expiración
     * @param timeUnit Unidad de tiempo
     */
    public void saveWithExpiration(Board board, long expirationTime, TimeUnit timeUnit) {
        String key = BOARD_KEY_PREFIX + board.getId();
        boardRedisTemplate.opsForValue().set(key, board, expirationTime, timeUnit);
    }
    
    /**
     * Obtiene un tablero de Redis
     * @param id ID del tablero
     * @return Tablero encontrado o null si no existe
     */
    public Board getBoardFromRedis(String id) {
        String key = BOARD_KEY_PREFIX + id;
        return boardRedisTemplate.opsForValue().get(key);
    }
} 