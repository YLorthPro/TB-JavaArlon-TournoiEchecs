package be.bstorm.formation.tournoiechecs.bll;

import be.bstorm.formation.tournoiechecs.bll.models.TournoiEnCoursException;
import be.bstorm.formation.tournoiechecs.bll.service.impl.TournoiServiceImpl;
import be.bstorm.formation.tournoiechecs.dal.model.Categorie;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

        assertEquals("Form peut pas être null", exception.getMessage());
    }

    @Test
    public void creationTournoi_formNonNull_EntitySaved() {
        TournoiForm form = new TournoiForm("Nom","Lieu",2,6,1000,2000, Set.of(Categorie.JUNIOR), Statut.EN_COURS,false, LocalDate.now().plusDays(10));

        tournoiService.creationTournoi(form);

        ArgumentCaptor<TournoiEntity> captor = ArgumentCaptor.forClass(TournoiEntity.class);
        verify(tournoiRepository).save(captor.capture());

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

    @Test
    void suppressionTournoiOK() {
        TournoiEntity tournoiEntity = new TournoiEntity();
        tournoiEntity.setStatut(Statut.TERMINE);

        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiEntity));

        tournoiService.suppressionTournoi(1L);

        verify(tournoiRepository, times(1)).delete(tournoiEntity);
    }

    @Test
    void suppressionTournoiEchoueCarTournoiNonTrouve() {
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> tournoiService.suppressionTournoi(1L));

        assertEquals("Tournoi non trouvé",exception.getMessage());
    }

    @Test
    void testSuppressionTournoiEchoueCarTournoiEnCours() {
        TournoiEntity tournoiEntity = new TournoiEntity();
        tournoiEntity.setStatut(Statut.EN_COURS);

        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiEntity));

        assertThrows(TournoiEnCoursException.class, () -> tournoiService.suppressionTournoi(1L));
    }

    @Test
    void top10() {
        List<TournoiEntity> tournois = new ArrayList<>();
        when(tournoiRepository.findTop10()).thenReturn(tournois);

        List<TournoiEntity> result = tournoiService.top10();

        assertEquals(tournois, result);
    }

    @Test
    void testRecherche() {
        Pageable pageable = PageRequest.of(0, 10);
        TournoiSearchForm form = new TournoiSearchForm("",Statut.EN_COURS,List.of(Categorie.JUNIOR));
        Page<TournoiEntity> tournois = new PageImpl<>(new ArrayList<>());

        when(tournoiRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(tournois);

        Page<TournoiEntity> result = tournoiService.recherche(form, pageable);

        verify(tournoiRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        assertEquals(tournois, result);
    }

    @Test
    void testGetTournoiById() {
        TournoiEntity tournoi = new TournoiEntity();
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoi));

        Optional<TournoiEntity> result = tournoiService.getTournoiById(1L);

        verify(tournoiRepository, times(1)).findById(anyLong());
        assertTrue(result.isPresent());
        assertEquals(tournoi, result.get());
    }

    @Test
    void testGetTournoiById_NotFound() {
        Long id = 1L;

        when(tournoiRepository.findById(id)).thenReturn(Optional.empty());

        Optional<TournoiEntity> result = tournoiService.getTournoiById(id);

        verify(tournoiRepository, times(1)).findById(id);
        assertTrue(result.isEmpty());
    }

}
