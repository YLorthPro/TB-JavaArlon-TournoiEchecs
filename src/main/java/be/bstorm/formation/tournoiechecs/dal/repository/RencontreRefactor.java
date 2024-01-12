package be.bstorm.formation.tournoiechecs.dal.repository;

import be.bstorm.formation.tournoiechecs.dal.model.RencontreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RencontreRefactor extends JpaRepository<RencontreEntity, Long> {
}
