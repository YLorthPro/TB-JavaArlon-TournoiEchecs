package be.bstorm.formation.tournoiechecs.dal.utils;

import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.*;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.RencontreRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class DataInit implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final JoueurRepository joueurRepository;
    private final TournoiService tournoiService;
    Faker faker = new Faker();
    private final TournoiRepository tournoiRepository;
    private final RencontreRepository rencontreRepository;

    public DataInit(PasswordEncoder passwordEncoder,
                    JoueurRepository joueurRepository, TournoiService tournoiService,
                    TournoiRepository tournoiRepository,
                    RencontreRepository rencontreRepository) {
        this.passwordEncoder = passwordEncoder;
        this.joueurRepository = joueurRepository;
        this.tournoiService = tournoiService;
        this.tournoiRepository = tournoiRepository;
        this.rencontreRepository = rencontreRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JoueurEntity checkmate = new JoueurEntity();
        checkmate.setPseudo("Mr Checkmate");
        checkmate.setMotDePasse(passwordEncoder.encode("Test1234="));
        checkmate.setELO(3000);
        checkmate.setRole(Role.ADMIN);
        checkmate.setEmail("checkmate@chess.be");
        checkmate.setGenre(Genre.GARCON);
        checkmate.setDateDeNaissance(LocalDate.now().minusYears(60));
        joueurRepository.save(checkmate);

        List<JoueurEntity> joueurs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            JoueurEntity joueur = new JoueurEntity();
            joueur.setPseudo(faker.name().firstName());
            joueur.setEmail(faker.internet().emailAddress());
            joueur.setDateDeNaissance(LocalDate.now().minusYears(20));
            joueur.setMotDePasse(passwordEncoder.encode("Test1234="));
            Random rand = new Random();
            int n = rand.nextInt(3);
            if(n == 0) {
                joueur.setGenre(Genre.GARCON);
            } else if(n == 1) {
                joueur.setGenre(Genre.FILLE);
            } else {
                joueur.setGenre(Genre.AUTRE);
            }
            joueurs.add(joueur);
            joueurRepository.save(joueur);
        }

        TournoiEntity tournoiToDelete = new TournoiEntity();
        tournoiToDelete.setWomenOnly(false);
        tournoiToDelete.setNom("Test");
        tournoiToDelete.setLieu("Arlon");
        tournoiToDelete.setCategories(Set.of(Categorie.SENIOR, Categorie.VETERAN));
        tournoiToDelete.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoiToDelete.setNombreMaxJoueurs(10);
        tournoiToDelete.setJoueurs(joueurs);
        tournoiToDelete.setDateCreation(LocalDate.now().minusDays(10));
        tournoiToDelete.setDateFinInscriptions(LocalDate.now().minusDays(1));
        tournoiRepository.save(tournoiToDelete);

        TournoiEntity tournoiEnCours = new TournoiEntity();
        tournoiEnCours.setWomenOnly(false);
        tournoiEnCours.setNom("Test city");
        tournoiEnCours.setLieu("Arlon");
        tournoiEnCours.setCategories(Set.of(Categorie.SENIOR));
        tournoiEnCours.setStatut(Statut.EN_COURS);
        tournoiEnCours.setRonde(3);
        tournoiEnCours.setNombreMaxJoueurs(2);
        tournoiEnCours.setNombreMaxJoueurs(4);
        tournoiEnCours.setJoueurs(List.of(joueurs.get(0),joueurs.get(1),joueurs.get(2),joueurs.get(3)));
        tournoiEnCours.setDateCreation(LocalDate.now().minusDays(10));
        tournoiEnCours.setDateFinInscriptions(LocalDate.now().minusDays(1));
        tournoiRepository.save(tournoiEnCours);

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setWomenOnly(false);
        tournoi.setNom("Test tournament");
        tournoi.setLieu("Arlon");
        tournoi.setCategories(Set.of(Categorie.JUNIOR,Categorie.SENIOR));
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi.setNombreMaxJoueurs(20);
        tournoi.setJoueurs(joueurs);
        tournoi.setDateCreation(LocalDate.now().minusDays(10));
        tournoi.setDateFinInscriptions(LocalDate.now().plusDays(1));
        tournoiRepository.save(tournoi);

        TournoiEntity tournoi2 = new TournoiEntity();
        tournoi2.setWomenOnly(false);
        tournoi2.setNom("Test tournament 2");
        tournoi2.setLieu("Arlon");
        tournoi2.setCategories(Set.of(Categorie.JUNIOR,Categorie.SENIOR));
        tournoi2.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi2.setNombreMaxJoueurs(20);
        tournoi2.setJoueurs(joueurs);
        tournoi2.setDateCreation(LocalDate.now().minusDays(10));
        tournoi2.setDateFinInscriptions(LocalDate.now().minusDays(1));
        tournoiRepository.save(tournoi2);

        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setTournoi(tournoiEnCours);
        rencontre.setJoueurBlanc(joueurs.get(0));
        rencontre.setJoueurNoir(joueurs.get(1));
        rencontre.setResultat(Resultat.BLANC);
        rencontre.setNumeroRonde(1);
        rencontreRepository.save(rencontre);

        RencontreEntity rencontre2 = new RencontreEntity();
        rencontre2.setTournoi(tournoiEnCours);
        rencontre2.setJoueurBlanc(joueurs.get(2));
        rencontre2.setJoueurNoir(joueurs.get(3));
        rencontre2.setResultat(Resultat.BLANC);
        rencontre2.setNumeroRonde(1);
        rencontreRepository.save(rencontre2);

        RencontreEntity rencontre3 = new RencontreEntity();
        rencontre3.setTournoi(tournoiEnCours);
        rencontre3.setJoueurBlanc(joueurs.get(0));
        rencontre3.setJoueurNoir(joueurs.get(2));
        rencontre3.setResultat(Resultat.BLANC);
        rencontre3.setNumeroRonde(2);
        rencontreRepository.save(rencontre3);

        RencontreEntity rencontre4 = new RencontreEntity();
        rencontre4.setTournoi(tournoiEnCours);
        rencontre4.setJoueurBlanc(joueurs.get(1));
        rencontre4.setJoueurNoir(joueurs.get(3));
        rencontre4.setResultat(Resultat.BLANC);
        rencontre4.setNumeroRonde(2);
        rencontreRepository.save(rencontre4);

        RencontreEntity rencontre5 = new RencontreEntity();
        rencontre5.setTournoi(tournoiEnCours);
        rencontre5.setJoueurBlanc(joueurs.get(0));
        rencontre5.setJoueurNoir(joueurs.get(3));
        rencontre5.setResultat(Resultat.BLANC);
        rencontre5.setNumeroRonde(3);
        rencontreRepository.save(rencontre5);

        RencontreEntity rencontre6 = new RencontreEntity();
        rencontre6.setTournoi(tournoiEnCours);
        rencontre6.setJoueurBlanc(joueurs.get(1));
        rencontre6.setJoueurNoir(joueurs.get(2));
        rencontre6.setResultat(Resultat.BLANC);
        rencontre6.setNumeroRonde(3);
        rencontreRepository.save(rencontre6);

        RencontreEntity rencontre7 = new RencontreEntity();
        rencontre7.setTournoi(tournoiEnCours);
        rencontre7.setJoueurBlanc(joueurs.get(1));
        rencontre7.setJoueurNoir(joueurs.get(0));
        rencontre7.setResultat(Resultat.BLANC);
        rencontre7.setNumeroRonde(4);
        rencontreRepository.save(rencontre7);

        RencontreEntity rencontre8 = new RencontreEntity();
        rencontre8.setTournoi(tournoiEnCours);
        rencontre8.setJoueurBlanc(joueurs.get(2));
        rencontre8.setJoueurNoir(joueurs.get(3));
        rencontre8.setResultat(Resultat.PAS_ENCORE_JOUEE);
        rencontre8.setNumeroRonde(4);
        rencontreRepository.save(rencontre8);
    }
}
