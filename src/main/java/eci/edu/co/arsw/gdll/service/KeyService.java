package eci.edu.co.arsw.gdll.service;

import eci.edu.co.arsw.gdll.model.Key;
import eci.edu.co.arsw.gdll.repository.KeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KeyService {

    @Autowired
    private KeyRepository keyRepository;

    public List<Key> getAllKeys() {
        return keyRepository.findAll();
    }

    public Optional<Key> getKeyById(Long id) {
        return keyRepository.findById(id);
    }

    public Key saveKey(Key key) {
        return keyRepository.save(key);
    }

    public void deleteKey(Long id) {
        keyRepository.deleteById(id);
    }

}
