package eci.edu.co.arsw.gdll.service;

import eci.edu.co.arsw.gdll.model.Level;
import eci.edu.co.arsw.gdll.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LevelService {

    @Autowired
    private LevelRepository levelRepository;

    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    public Optional<Level> getLevelById(Long id) {
        return levelRepository.findById(id);
    }

    public Level saveLevel(Level level) {
        return levelRepository.save(level);
    }

    public void deleteLevel(Long id) {
        levelRepository.deleteById(id);
    }

}
