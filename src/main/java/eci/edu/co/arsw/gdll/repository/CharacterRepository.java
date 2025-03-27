package eci.edu.co.arsw.gdll.repository;

import eci.edu.co.arsw.gdll.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}
