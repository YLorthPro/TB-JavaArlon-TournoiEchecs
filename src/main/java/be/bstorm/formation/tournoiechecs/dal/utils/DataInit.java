package be.bstorm.formation.tournoiechecs.dal.utils;

import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Role;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInit implements InitializingBean {

    private final PasswordEncoder passwordEncoder;
    private final JoueurRepository joueurRepository;

    public DataInit(PasswordEncoder passwordEncoder,
                    JoueurRepository joueurRepository) {
        this.passwordEncoder = passwordEncoder;
        this.joueurRepository = joueurRepository;
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

    }
}
