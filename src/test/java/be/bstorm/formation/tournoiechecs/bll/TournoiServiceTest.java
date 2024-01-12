package be.bstorm.formation.tournoiechecs.bll;

import be.bstorm.formation.tournoiechecs.bll.service.impl.TournoiServiceImpl;
import be.bstorm.formation.tournoiechecs.dal.model.Categorie;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TournoiServiceTest {
    @InjectMocks
    TournoiServiceImpl tournoiService;

    @Mock
    TournoiRepository tournoiRepository;

    @Test
    public void creationTournoi_formNull() {
        TournoiForm form = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> tournoiService.creationTournoi(form));

        assertEquals("Form ne peut être null", exception.getMessage());
    }

    @Test
    public void creationTournoi_formNonNull_EntitySaved() {
        TournoiForm form = new TournoiForm("Nom","Lieu",2,6,1000,2000, Set.of(Categorie.JUNIOR), Statut.EN_COURS,false, LocalDate.now().plusDays(10));

        tournoiService.creationTournoi(form);

        ArgumentCaptor<TournoiEntity> captor = ArgumentCaptor.forClass(TournoiEntity.class);
        Mockito.verify(tournoiRepository).save(captor.capture());

        TournoiEntity savedEntity = captor.getValue();

        assertEquals(form.nom(), savedEntity.getNom());
        assertEquals(form.lieu(), savedEntity.getLieu());
        assertEquals(form.nombreMinJoueurs(), savedEntity.getNombreMinJoueurs());
        assertEquals(form.nombreMaxJoueurs(), savedEntity.getNombreMaxJoueurs());
        assertEquals(form.eLOMin(), savedEntity.getELOMin());
        assertEquals(form.eLOMax(), savedEntity.getELOMax());
        assertEquals(form.categories(), savedEntity.getCategories());
        assertEquals(form.statut(), savedEntity.getStatut());
        assertEquals(form.womenOnly(), savedEntity.isWomenOnly());
        assertEquals(form.dateFinInscriptions(), savedEntity.getDateFinInscriptions());
        assertEquals(LocalDate.now(), savedEntity.getDateCreation());
        assertEquals(LocalDate.now(), savedEntity.getDateModification());
    }
}
