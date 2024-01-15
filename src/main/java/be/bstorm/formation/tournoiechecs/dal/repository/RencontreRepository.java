package be.bstorm.formation.tournoiechecs.dal.repository;

import be.bstorm.formation.tournoiechecs.dal.model.RencontreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RencontreRepository extends JpaRepository<RencontreEntity, Long>, JpaSpecificationExecutor<RencontreEntity> {
}
