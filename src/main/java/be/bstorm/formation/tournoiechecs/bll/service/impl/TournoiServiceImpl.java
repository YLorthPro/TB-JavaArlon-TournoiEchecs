package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.models.TournoiEnCoursException;
import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TournoiServiceImpl implements TournoiService {

    private final TournoiRepository tournoiRepository;
    private final JoueurRepository joueurRepository;

    public TournoiServiceImpl(TournoiRepository tournoiRepository,
                              JoueurRepository joueurRepository) {
        this.tournoiRepository = tournoiRepository;
        this.joueurRepository = joueurRepository;
    }


    @Override
    public void creationTournoi(TournoiForm form) {
        if (form == null)
            throw new IllegalArgumentException("Form peut pas être null");

        TournoiEntity entity = new TournoiEntity();
        entity.setNom(form.nom());
        entity.setLieu(form.lieu());
        entity.setNombreMinJoueurs(form.nombreMinJoueurs());
        entity.setNombreMaxJoueurs(form.nombreMaxJoueurs());
        entity.setELOMin(form.eLOMin());
        entity.setELOMax(form.eLOMax());
        entity.setCategories(form.categories());
        entity.setStatut(form.statut());
        entity.setRonde(0);
        entity.setWomenOnly(form.womenOnly());
        entity.setDateFinInscriptions(form.dateFinInscriptions());
        entity.setDateCreation(LocalDate.now());
        entity.setDateModification(LocalDate.now());

        tournoiRepository.save(entity);

    }

    @Override
    public void suppressionTournoi(Long id) {
        TournoiEntity entity = tournoiRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Tournoi non trouvé"));

        if(entity.getStatut()== Statut.EN_COURS)
            throw new TournoiEnCoursException();

        tournoiRepository.delete(entity);
    }

    @Override
    public List<TournoiEntity> top10() {
        return tournoiRepository.findTop10();
    }

    @Override
    public Page<TournoiEntity> recherche(TournoiSearchForm form, Pageable pageable) {
        return tournoiRepository.findAll(specification(form),pageable);
    }

    public Optional<TournoiEntity> getTournoiById(Long id) {
        return tournoiRepository.findById(id);
    }

    public Boolean canRegister(Long tournoiId, Long joueurId){

        // Récupérer les entités de la base de données
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));
        JoueurEntity joueur = joueurRepository.findById(joueurId).orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));

        if(tournoi.getStatut() != Statut.EN_ATTENTE_DE_JOUEURS)
            return false;

        // Vérifier si le tournoi a atteint le nombre maximum de joueurs
        if(tournoi.getJoueurs().size() >= tournoi.getNombreMaxJoueurs())
            return false;


        // Vérifier si l'Elo du joueur est dans la plage acceptable pour le tournoi
        if(joueur.getELO() < tournoi.getELOMin() || joueur.getELO() > tournoi.getELOMax())
            return false;


        // Vérifier si le tournoi est femmes seulement et si c'est le cas, vérifier si le joueur est une femme
        if(tournoi.isWomenOnly() && joueur.getGenre() != Genre.FILLE)
            return false;


        return !isRegistered(tournoiId, joueurId);
    }

    public Boolean isRegistered(Long tournoiId, Long joueurId){
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));
        JoueurEntity joueur = joueurRepository.findById(joueurId).orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));

        return tournoi.getJoueurs().contains(joueur);
    }

    private Specification<TournoiEntity> specification (TournoiSearchForm form){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (form.nom() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), "%" + form.nom().toLowerCase() + "%"));
            }

            if (form.statut() != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), form.statut()));
            }

            if (form.categories() != null && !form.categories().isEmpty()) {
                predicates.add(root.get("categories").in(form.categories()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}
