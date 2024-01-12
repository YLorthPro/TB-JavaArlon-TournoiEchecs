package be.bstorm.formation.tournoiechecs.dal.repository;

import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JoueurRepository extends JpaRepository<JoueurEntity, Long> {
    Optional<JoueurEntity> findByPseudoOrEmail(String pseudo, String Email);
}
