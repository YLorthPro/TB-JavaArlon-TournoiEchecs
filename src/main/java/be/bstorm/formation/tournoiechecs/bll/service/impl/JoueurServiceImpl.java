package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.service.JoueurService;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.pl.config.security.JWTProvider;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.LoginForm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class JoueurServiceImpl implements JoueurService {

    private final JoueurRepository joueurRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JoueurServiceImpl(JoueurRepository joueurRepository, AuthenticationManager authenticationManager, JWTProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.joueurRepository = joueurRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
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

    @Override
    public String login(LoginForm form) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(form.identifiant(),form.motDePasse()));

        JoueurEntity joueur = joueurRepository.findByPseudoOrEmail(form.identifiant(),form.identifiant())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return jwtProvider.generateToken(joueur.getUsername(), joueur.getRole());
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
