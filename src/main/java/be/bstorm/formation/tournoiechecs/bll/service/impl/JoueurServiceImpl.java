package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.service.JoueurService;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class JoueurServiceImpl implements JoueurService {

    private final JoueurRepository joueurRepository;

    public JoueurServiceImpl(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }


    @Override
    public void inscription(JoueurForm joueur) {
        if (joueur == null)
            throw new IllegalArgumentException("form peut pas être null");

        JoueurEntity entity = new JoueurEntity();
        entity.setPseudo(joueur.pseudo());
        entity.setEmail(joueur.email());
        entity.setMotDePasse(setMotDePasse());
        entity.setDateDeNaissance(joueur.dateDeNaissance());
        entity.setGenre(joueur.genre());
        if(joueur.eLO() == null)
            entity.setELO(1200);
        else
            entity.setELO(joueur.eLO());
        entity.setRole(joueur.role());

        joueurRepository.save(entity);
    }

    private String setMotDePasse() {
        SecureRandom random = new SecureRandom();
        int passwordLength = 10;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()_+";

        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
