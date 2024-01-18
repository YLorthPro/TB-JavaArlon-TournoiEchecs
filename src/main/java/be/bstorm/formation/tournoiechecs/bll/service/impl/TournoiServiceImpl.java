package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.models.exception.InscriptionTournoiException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.RencontreException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.TournoiEnCoursException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.TournoiException;
import be.bstorm.formation.tournoiechecs.bll.models.models.JoueurScore;
import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.*;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.RencontreRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TournoiServiceImpl implements TournoiService {

    private final TournoiRepository tournoiRepository;
    private final JoueurRepository joueurRepository;
    private final RencontreRepository rencontreRepository;
    private final EmailServiceImpl emailService;

    public TournoiServiceImpl(TournoiRepository tournoiRepository,
                              JoueurRepository joueurRepository,
                              RencontreRepository rencontreRepository, EmailServiceImpl emailService) {
        this.tournoiRepository = tournoiRepository;
        this.joueurRepository = joueurRepository;
        this.rencontreRepository = rencontreRepository;
        this.emailService = emailService;
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

        TournoiEntity finalEntity = tournoiRepository.save(entity);

        joueurRepository.findAll(joueurSpecification(finalEntity))
                .forEach(joueur -> {
                    try {
                        emailService.nouveauTournoiCree(joueur, finalEntity);
                    } catch(MessagingException ex){
                        System.out.println("Erreur mail: " + ex.getMessage());
                    }
                });
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

        Page<TournoiEntity> originalPage = tournoiRepository.findAll(tournoiSpecification(form), pageable);
        List<TournoiEntity> filteredList = originalPage.stream()
                .filter(tournoiEntity -> tournoiEntity.getCategories().containsAll(form.categories()))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredList, pageable, filteredList.size());
    }

    @Override
    public Optional<TournoiEntity> getTournoiById(Long id) {
        return tournoiRepository.findById(id);
    }

    @Override
    public void inscriptionTournoi(Long tournoiId, String login) {
        if(!tournoiRepository.existsById(tournoiId))
            throw new EntityNotFoundException("Tournoi non trouvé");

        JoueurEntity joueur = joueurRepository.findByPseudoOrEmail(login, login).orElseThrow(()->new EntityNotFoundException("Joueur non trouvé"));

        if (!canRegister(tournoiId, joueur.getId()))
            throw new InscriptionTournoiException("Impossible de s'inscrire au tournoi");

        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(()->new EntityNotFoundException("Tournoi non trouvé!"));
        tournoi.getJoueurs().add(joueur);
        tournoiRepository.save(tournoi);

    }

    @Override
    public void desinscriptionTournoi(Long tournoiId, String login) {
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));
        JoueurEntity joueur = joueurRepository.findByPseudoOrEmail(login, login).orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));
        if(!isRegistered(tournoiId, joueur.getId()) || tournoi.getStatut()!=Statut.EN_ATTENTE_DE_JOUEURS)
            throw new InscriptionTournoiException("Impossible de se désinscrire du tournoi");

        tournoi.getJoueurs().remove(joueur);
        tournoiRepository.save(tournoi);
    }

    @Override
    public void demarrerTournoi(Long tournoiId){

        // Récupérer le tournoi en fonction de l'ID
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));

        // Vérifier si le nombre minimum de participants est atteint
        if(tournoi.getJoueurs().size() < tournoi.getNombreMinJoueurs())
            throw new TournoiException("Nombre minimum de participants non atteint");

        // Vérifier si la date de fin des inscriptions est dépassée
        if (tournoi.getDateFinInscriptions().isAfter(LocalDate.now()))
            throw new TournoiException("La date de fin des inscriptions n'est pas dépassée");

        // Initialisation du tournoi
        tournoi.setRonde(1);
        tournoi.setDateModification(LocalDate.now());

        // Générer toutes les rencontres pour un Round Robin
        List<JoueurEntity> joueurs = new ArrayList<>(tournoi.getJoueurs());

        // Le nombre de joueurs doit être pair pour un Round Robin
        boolean isNombreJoueursImpair = joueurs.size() % 2 != 0;
        if (isNombreJoueursImpair) {
            joueurs.add(null);
        }

        // Le nombre total des rondes est égal au nombre de joueurs moins un
        int numRondes = joueurs.size() - 1;

        for (int ronde = 0; ronde < numRondes; ronde++) {
            for (int i = 0; i < joueurs.size() / 2; i++) {
                JoueurEntity joueurBlanc = joueurs.get(i);
                JoueurEntity joueurNoir = joueurs.get(joueurs.size() - 1 - i);

                // Ne créer une rencontre que si les deux joueurs sont réels
                if (joueurBlanc != null && joueurNoir != null) {
                    // Créer une rencontre aller
                    createRencontre(tournoi, joueurBlanc, joueurNoir, ronde + 1);
                    // Créer une rencontre retour
                    createRencontre(tournoi, joueurNoir, joueurBlanc, ronde + numRondes + 1);
                }
            }
            // Rotation des joueurs, à l'exception du premier
            joueurs.add(1, joueurs.remove(joueurs.size() - 1));
        }

        tournoi.setStatut(Statut.EN_COURS);
        tournoiRepository.save(tournoi);
    }

    @Override
    public void modifierResultatRencontre(Long rencontreId, Resultat resultat) {
        RencontreEntity rencontre = rencontreRepository.findById(rencontreId).orElseThrow(() -> new EntityNotFoundException("Rencontre non trouvée"));
        TournoiEntity tournoi = rencontre.getTournoi();

        if (rencontre.getNumeroRonde()!=tournoi.getRonde()) {
            throw new RencontreException("On ne peut modifier le résultat d'une rencontre que si elle fait partie de la ronde courante");
        }

        rencontre.setResultat(resultat);
        rencontreRepository.save(rencontre);
    }

    @Override
    public void passerTourSuivant(Long tournoiId) {
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));

        Specification<RencontreEntity> specification = (root, query, criteriaBuilder)-> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("tournoi").get("id"), tournoiId),
                criteriaBuilder.equal(root.get("numeroRonde"),tournoi.getRonde())
        );

        List<RencontreEntity> rencontres = rencontreRepository.findAll(specification);
        for (RencontreEntity rencontre : rencontres) {
            if (rencontre.getResultat() == Resultat.PAS_ENCORE_JOUEE) {
                throw new TournoiException("Toutes les rencontres de la ronde courante doivent avoir été jouées avant de pouvoir passer à la ronde suivante");
            }
        }

        tournoi.setRonde(tournoi.getRonde() + 1);
        tournoiRepository.save(tournoi);
    }

    @Override
    public List<JoueurScore> afficherTableauScores(Long tournoiId, int ronde) {

        List<JoueurEntity> joueurs = tournoiRepository.findById(tournoiId).orElseThrow(()-> new EntityNotFoundException("Tournoi non trouvé")).getJoueurs();

        Specification<RencontreEntity> spec = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.equal(root.get("tournoi").get("id"),tournoiId), criteriaBuilder.lessThanOrEqualTo(root.get("numeroRonde"), ronde));

        List<RencontreEntity> rencontresRonde = rencontreRepository.findAll(spec);

        List<JoueurScore> tableauScores = new ArrayList<>();

        joueurs.forEach(j->{

            JoueurScore joueur = new JoueurScore();
            joueur.setNom(j.getPseudo());

            List<RencontreEntity> rencontresJouees = rencontresRonde.stream()
                    .filter(rencontreEntity -> rencontreEntity.getJoueurBlanc().equals(j)||rencontreEntity.getJoueurNoir().equals(j))
                    .toList();

            for (RencontreEntity rencontresJouee : rencontresJouees) {
                joueur.setRencontreJouees(joueur.getRencontreJouees()+1);
                if(rencontresJouee.getResultat()==Resultat.EGALITE){
                    joueur.setScore(joueur.getScore()+0.5);
                    joueur.setEgalite(joueur.getEgalite()+1);
                } else if ((rencontresJouee.getResultat()==Resultat.BLANC && rencontresJouee.getJoueurBlanc().equals(j)) || (rencontresJouee.getResultat()==Resultat.NOIR && rencontresJouee.getJoueurNoir().equals(j))){
                    joueur.setScore(joueur.getScore()+1);
                    joueur.setVictoires(joueur.getVictoires()+1);
                } else if((rencontresJouee.getResultat()==Resultat.NOIR && rencontresJouee.getJoueurBlanc().equals(j)) || (rencontresJouee.getResultat()==Resultat.BLANC && rencontresJouee.getJoueurNoir().equals(j)))
                    joueur.setDefaites(joueur.getDefaites()+1);
            }

            tableauScores.add(joueur);

        });

        return tableauScores;
    }

    private void createRencontre(TournoiEntity tournoi, JoueurEntity joueurBlanc, JoueurEntity joueurNoir, int ronde) {
        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setTournoi(tournoi);
        rencontre.setJoueurBlanc(joueurBlanc);
        rencontre.setJoueurNoir(joueurNoir);
        rencontre.setNumeroRonde(ronde);
        rencontreRepository.save(rencontre);
    }

    public boolean canRegister(Long tournoiId, Long joueurId){

        // Récupérer les entités de la base de données
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));
        JoueurEntity joueur = joueurRepository.findById(joueurId).orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));

        // Vérifier si le tournoi n'a pas commencé
        if(tournoi.getStatut() != Statut.EN_ATTENTE_DE_JOUEURS)
            return false;

        // Vérifier que la date d'inscription n'est pas dépassée
        if (tournoi.getDateFinInscriptions().isBefore(LocalDate.now()))
            return false;

        // Vérifier si le tournoi a atteint le nombre maximum de joueurs
        if(tournoi.getJoueurs().size() >= tournoi.getNombreMaxJoueurs())
            return false;

        // Vérifier si l'Elo du joueur est dans la plage acceptable pour le tournoi
        if(tournoi.getELOMax()!= null && joueur.getELO() > tournoi.getELOMax())
            return false;

        if(tournoi.getELOMin()!= null && joueur.getELO() < tournoi.getELOMin())
            return false;

        // Vérifier si le tournoi est femmes seulement et si c'est le cas, vérifier si le joueur est une femme
        if(tournoi.isWomenOnly() && joueur.getGenre() == Genre.GARCON)
            return false;

        // Vérifier si le tournoi a bien la catégorie d'âge du joueur
        Period period = Period.between(joueur.getDateDeNaissance(), tournoi.getDateFinInscriptions());
        int age = period.getMonths();
        Categorie categorie;

        if (age < 18) {
            categorie = Categorie.JUNIOR;
        } else if (age < 60) {
            categorie = Categorie.SENIOR;
        } else {
            categorie = Categorie.VETERAN;
        }

        if (!tournoi.getCategories().contains(categorie))
            return false;


        return !isRegistered(tournoiId, joueurId);
    }

    public boolean isRegistered(Long tournoiId, Long joueurId){
        TournoiEntity tournoi = tournoiRepository.findById(tournoiId).orElseThrow(() -> new EntityNotFoundException("Tournoi non trouvé"));
        JoueurEntity joueur = joueurRepository.findById(joueurId).orElseThrow(() -> new EntityNotFoundException("Joueur non trouvé"));

        return tournoi.getJoueurs().contains(joueur);
    }

    private Specification<TournoiEntity> tournoiSpecification(TournoiSearchForm form){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (form.nom() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), "%" + form.nom().toLowerCase() + "%"));
            }

            if (form.statut() != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), form.statut()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<JoueurEntity> joueurSpecification(TournoiEntity tournoi) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ELO du joueur
            if(tournoi.getELOMax()!= null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eLO"), tournoi.getELOMax()));
            }

            if(tournoi.getELOMin()!= null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eLO"), tournoi.getELOMin()));
            }

            // Tournoi féminin seulement
            if(tournoi.isWomenOnly()){
                predicates.add(criteriaBuilder.notEqual(root.get("genre"), Genre.GARCON));
            }

            if (tournoi.getCategories() != null && !tournoi.getCategories().isEmpty()) {
                List<Predicate> categoryPredicates = new ArrayList<>();
                if(tournoi.getCategories().contains(Categorie.JUNIOR))
                    categoryPredicates.add(criteriaBuilder.greaterThan(root.get("dateDeNaissance"), tournoi.getDateFinInscriptions().minusYears(18)));
                if(tournoi.getCategories().contains(Categorie.SENIOR)) {
                    Predicate seniorMinAge = criteriaBuilder.lessThanOrEqualTo(root.get("dateDeNaissance"), tournoi.getDateFinInscriptions().minusYears(18));
                    Predicate seniorMaxAge = criteriaBuilder.greaterThan(root.get("dateDeNaissance"), tournoi.getDateFinInscriptions().minusYears(60));
                    categoryPredicates.add(criteriaBuilder.and(seniorMinAge, seniorMaxAge));
                }
                if(tournoi.getCategories().contains(Categorie.VETERAN))
                    categoryPredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateDeNaissance"), tournoi.getDateFinInscriptions().minusYears(60)));

                predicates.add(criteriaBuilder.or(categoryPredicates.toArray(new Predicate[0])));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
