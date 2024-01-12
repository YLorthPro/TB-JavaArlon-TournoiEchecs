package be.bstorm.formation.tournoiechecs.dal.repository;

import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TournoiRepository extends JpaRepository<TournoiEntity, Long>, JpaSpecificationExecutor<TournoiEntity> {

    @Query("SELECT t FROM TournoiEntity t Where t.statut != 'TERMINE' order by t.dateModification desc limit 10")
    List<TournoiEntity> findTop10();
}
