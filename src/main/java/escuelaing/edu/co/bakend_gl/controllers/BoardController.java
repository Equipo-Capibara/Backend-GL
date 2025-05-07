package escuelaing.edu.co.bakend_gl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.services.BoardService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/create")
    public ResponseEntity<Board> createBoard(@RequestParam("width") int width, 
                                           @RequestParam("height") int height) {
        Board newBoard = boardService.createBoard(width, height);
        
        // Guardar también en Redis con tiempo de expiración de 1 hora
        boardService.saveWithExpiration(newBoard, 1, TimeUnit.HOURS);
        
        return new ResponseEntity<>(newBoard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable("id") String id) {
        // Primero intentamos obtener de Redis por rendimiento
        Board board = boardService.getBoardFromRedis(id);
        
        // Si no está en Redis, buscamos en el repositorio principal
        if (board == null) {
            Optional<Board> optionalBoard = boardService.getBoardById(id);
            if (optionalBoard.isPresent()) {
                board = optionalBoard.get();
                // Lo guardamos en Redis para futuras consultas
                boardService.saveWithExpiration(board, 1, TimeUnit.HOURS);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        
        return new ResponseEntity<>(board, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Board>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        return new ResponseEntity<>(boards, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Board> updateBoard(@RequestBody Board board) {
        if (board.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Board updatedBoard = boardService.updateBoard(board);
        
        // Actualizamos también en Redis
        boardService.saveWithExpiration(updatedBoard, 1, TimeUnit.HOURS);
        
        return new ResponseEntity<>(updatedBoard, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") String id) {
        boardService.deleteBoard(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 