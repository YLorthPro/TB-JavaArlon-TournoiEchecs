package be.bstorm.formation.tournoiechecs.bll.service;

import be.bstorm.formation.tournoiechecs.bll.models.models.JoueurScore;
import be.bstorm.formation.tournoiechecs.dal.model.Resultat;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TournoiService {

    void creationTournoi(TournoiForm form);
    void suppressionTournoi(Long id);
    List<TournoiEntity> top10();
    Page<TournoiEntity> recherche(TournoiSearchForm form, Pageable pageable);
    Optional<TournoiEntity> getTournoiById(Long id);
    void inscriptionTournoi(Long tournoiId, String login);
    void desinscriptionTournoi(Long tournoiId, String login);
    void demarrerTournoi(Long tournoiId);
    void modifierResultatRencontre(Long rencontreId, Resultat resultat);
    void passerTourSuivant(Long tournoiId);
    List<JoueurScore>afficherTableauScores(Long tournoiId, int ronde);
}
