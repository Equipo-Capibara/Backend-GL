package escuelaing.edu.co.bakend_gl.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import escuelaing.edu.co.bakend_gl.model.board.Board;
import escuelaing.edu.co.bakend_gl.repository.BoardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final RedisTemplate<String, Board> redisTemplate;
    private final String KEY_PREFIX = "board:";

    @Autowired
    public BoardRepositoryImpl(RedisTemplate<String, Board> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <S extends Board> S save(S board) {
        if (board.getId() == null) {
            throw new IllegalArgumentException("Board ID cannot be null");
        }
        redisTemplate.opsForValue().set(KEY_PREFIX + board.getId(), board);
        return board;
    }

    @Override
    public <S extends Board> Iterable<S> saveAll(Iterable<S> boards) {
        List<S> savedBoards = new ArrayList<>();
        boards.forEach(board -> savedBoards.add(save(board)));
        return savedBoards;
    }

    @Override
    public Optional<Board> findById(String id) {
        Board board = redisTemplate.opsForValue().get(KEY_PREFIX + id);
        return Optional.ofNullable(board);
    }

    @Override
    public boolean existsById(String id) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + id));
    }

    @Override
    public Iterable<Board> findAll() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        List<Board> boards = new ArrayList<>();
        
        if (keys != null) {
            keys.forEach(key -> {
                Board board = redisTemplate.opsForValue().get(key);
                if (board != null) {
                    boards.add(board);
                }
            });
        }
        
        return boards;
    }

    @Override
    public Iterable<Board> findAllById(Iterable<String> ids) {
        List<Board> boards = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(boards::add));
        return boards;
    }

    @Override
    public long count() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        return keys != null ? keys.size() : 0;
    }

    @Override
    public void deleteById(String id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }

    @Override
    public void delete(Board board) {
        deleteById(board.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends String> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Board> boards) {
        boards.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }
} 