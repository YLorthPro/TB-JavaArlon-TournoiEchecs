package be.bstorm.formation.tournoiechecs.dal.repository;

import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournoiRepository extends JpaRepository<TournoiEntity, Long> {
}
