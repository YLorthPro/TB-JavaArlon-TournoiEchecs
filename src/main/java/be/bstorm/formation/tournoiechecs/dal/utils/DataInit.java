package be.bstorm.formation.tournoiechecs.dal.utils;

import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.*;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataInit implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final JoueurRepository joueurRepository;

    Faker faker = new Faker();
    private final TournoiRepository tournoiRepository;

    public DataInit(PasswordEncoder passwordEncoder,
                    JoueurRepository joueurRepository,
                    TournoiRepository tournoiRepository) {
        this.passwordEncoder = passwordEncoder;
        this.joueurRepository = joueurRepository;
        this.tournoiRepository = tournoiRepository;
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
            joueurs.add(joueur);
            joueurRepository.save(joueur);
        }

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setWomenOnly(false);
        tournoi.setNom("Test tournament");
        tournoi.setLieu("Arlon");
        tournoi.setCategories(Set.of(Categorie.JUNIOR,Categorie.SENIOR));
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi.setNombreMaxJoueurs(20);
        tournoi.setJoueurs(joueurs);
        tournoi.setDateCreation(LocalDate.now().minusDays(10));
        tournoi.setDateFinInscriptions(LocalDate.now().minusDays(1));
        tournoiRepository.save(tournoi);

    }
}
