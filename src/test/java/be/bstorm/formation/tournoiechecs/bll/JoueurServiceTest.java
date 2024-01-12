package be.bstorm.formation.tournoiechecs.bll;

import be.bstorm.formation.tournoiechecs.bll.service.impl.JoueurServiceImpl;
import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Role;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JoueurServiceTest {

    @InjectMocks
    JoueurServiceImpl joueurService;

    @Mock
    JoueurRepository joueurRepository;

    @Test
    public void testInscription_WithELO() {
        JoueurForm joueurForm = new JoueurForm("John","johnDoe@gmail.com","Test1234=", LocalDate.now().minusYears(20), Genre.GARCON, 1000, Role.JOUEUR);

        JoueurEntity joueurEntity = new JoueurEntity();
        joueurEntity.setPseudo("John");
        joueurEntity.setEmail("johnDoe@gmail.com");
        joueurEntity.setMotDePasse("Test1234=");
        joueurEntity.setDateDeNaissance(LocalDate.now().minusYears(20));
        joueurEntity.setGenre(Genre.GARCON);
        joueurEntity.setRole(Role.JOUEUR);
        joueurEntity.setELO(1000);
        Mockito.when(joueurRepository.save(Mockito.any(JoueurEntity.class))).thenReturn(joueurEntity);

       joueurService.inscription(joueurForm);

        Mockito.verify(joueurRepository, Mockito.times(1)).save(Mockito.any(JoueurEntity.class));
    }

    @Test
    public void testInscription_NoELO() {

        JoueurForm joueurForm = new JoueurForm("John","johnDoe@gmail.com","Test1234=", LocalDate.now().minusYears(20), Genre.GARCON, null, Role.JOUEUR);
        Mockito.when(joueurRepository.save(Mockito.any(JoueurEntity.class))).thenReturn(null);

        joueurService.inscription(joueurForm);

        Mockito.verify(joueurRepository, Mockito.times(1)).save(Mockito.any(JoueurEntity.class));

        ArgumentCaptor<JoueurEntity> argumentCaptor = ArgumentCaptor.forClass(JoueurEntity.class);
        Mockito.verify(joueurRepository).save(argumentCaptor.capture());
        JoueurEntity capturedJoueur = argumentCaptor.getValue();
        assertEquals(1200, capturedJoueur.getELO());
    }

    @Test
    public void testInscription_NullJoueurForm() {
        JoueurForm joueurForm = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> joueurService.inscription(joueurForm));

        assertEquals("form peut pas être null", exception.getMessage());
    }
}
