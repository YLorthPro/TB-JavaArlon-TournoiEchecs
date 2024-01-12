package be.bstorm.formation.tournoiechecs.bll;

import be.bstorm.formation.tournoiechecs.bll.service.impl.JoueurServiceImpl;
import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Role;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.pl.config.security.JWTProvider;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.LoginForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JoueurServiceTest {

    @InjectMocks
    JoueurServiceImpl joueurService;

    @Mock
    JoueurRepository joueurRepository;
    @Mock
    JWTProvider jwtProvider;
    @Mock
    AuthenticationManager authenticationManager;

    @Test
    public void testInscription_WithELO() {
        JoueurForm joueurForm = new JoueurForm("John","johnDoe@gmail.com", LocalDate.now().minusYears(20), Genre.GARCON, 1000, Role.JOUEUR);

        JoueurEntity joueurEntity = new JoueurEntity();
        joueurEntity.setPseudo("John");
        joueurEntity.setEmail("johnDoe@gmail.com");
        joueurEntity.setMotDePasse("Test1234=");
        joueurEntity.setDateDeNaissance(LocalDate.now().minusYears(20));
        joueurEntity.setGenre(Genre.GARCON);
        joueurEntity.setRole(Role.JOUEUR);
        joueurEntity.setELO(1000);
        when(joueurRepository.save(any(JoueurEntity.class))).thenReturn(joueurEntity);

       joueurService.inscription(joueurForm);

        verify(joueurRepository, Mockito.times(1)).save(any(JoueurEntity.class));
    }

    @Test
    public void testInscription_NoELO() {

        JoueurForm joueurForm = new JoueurForm("John","johnDoe@gmail.com", LocalDate.now().minusYears(20), Genre.GARCON, null, Role.JOUEUR);
        when(joueurRepository.save(any(JoueurEntity.class))).thenReturn(null);

        joueurService.inscription(joueurForm);

        verify(joueurRepository, Mockito.times(1)).save(any(JoueurEntity.class));

        ArgumentCaptor<JoueurEntity> argumentCaptor = ArgumentCaptor.forClass(JoueurEntity.class);
        verify(joueurRepository).save(argumentCaptor.capture());
        JoueurEntity capturedJoueur = argumentCaptor.getValue();
        assertEquals(1200, capturedJoueur.getELO());
    }

    @Test
    public void testInscription_NullJoueurForm() {
        JoueurForm joueurForm = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> joueurService.inscription(joueurForm));

        assertEquals("form peut pas être null", exception.getMessage());
    }

    @Test
    public void testLogin_OK() {

        LoginForm form = new LoginForm("identifiant", "motDePasse");
        JoueurEntity joueur = new JoueurEntity();
        joueur.setPseudo("nomUtilisateur");
        joueur.setRole(Role.JOUEUR);
        when(joueurRepository.findByPseudoOrEmail(form.identifiant(), form.identifiant())).thenReturn(Optional.of(joueur));
        when(jwtProvider.generateToken(joueur.getUsername(), joueur.getRole())).thenReturn("token");

        joueurService.login(form);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(form.identifiant(), form.motDePasse()));
        verify(joueurRepository).findByPseudoOrEmail(form.identifiant(), form.identifiant());
        verify(jwtProvider).generateToken(joueur.getUsername(), joueur.getRole());
    }

    @Test
    void testLogin_IdentifiantNotExists() {
        LoginForm form = new LoginForm("identifiant", "motDePasse");
        when(joueurRepository.findByPseudoOrEmail(form.identifiant(), form.identifiant())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            joueurService.login(form);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void testLogin_BadPassword() {

        LoginForm form = new LoginForm("identifiant", "motDePasse");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Mauvais mot de passe"));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            joueurService.login(form);
        });

        assertEquals("Mauvais mot de passe", exception.getMessage());
    }


}
