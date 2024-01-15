package be.bstorm.formation.tournoiechecs.bll;

import be.bstorm.formation.tournoiechecs.bll.models.InscriptionTournoiException;
import be.bstorm.formation.tournoiechecs.bll.models.RencontreException;
import be.bstorm.formation.tournoiechecs.bll.models.TournoiEnCoursException;
import be.bstorm.formation.tournoiechecs.bll.models.TournoiException;
import be.bstorm.formation.tournoiechecs.bll.service.impl.TournoiServiceImpl;
import be.bstorm.formation.tournoiechecs.dal.model.*;
import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import be.bstorm.formation.tournoiechecs.dal.repository.RencontreRepository;
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
    RencontreRepository rencontreRepository;

    @Mock
    TournoiRepository tournoiRepository;

    @Mock
    JoueurRepository joueurRepository;

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

    @Test
    void testInscriptionTournoi_OK() {

        Long tournoiId = 1L;
        Long joueurId = 1L;
        String login = "test";

        JoueurEntity joueur = new JoueurEntity();
        joueur.setId(joueurId);
        joueur.setPseudo(login);
        joueur.setEmail(login);
        joueur.setGenre(Genre.GARCON);
        joueur.setDateDeNaissance(LocalDate.now().minusYears(17));

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setDateFinInscriptions(LocalDate.now().plusDays(1));
        tournoi.setCategories(Set.of(Categorie.JUNIOR));
        tournoi.setELOMin(null);
        tournoi.setELOMax(null);
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi.setNombreMaxJoueurs(10);

        when(tournoiRepository.existsById(tournoiId)).thenReturn(true);
        when(joueurRepository.findByPseudoOrEmail(eq(login), eq(login))).thenReturn(Optional.of(joueur));
        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findById(joueurId)).thenReturn(Optional.of(joueur));

        tournoiService .inscriptionTournoi(tournoiId, login);

        assertTrue(tournoi.getJoueurs().contains(joueur));
        verify(tournoiRepository, times(1)).save(tournoi);

    }

    @Test
    void testInscriptionTournoiCantRegister() {

        Long tournoiId = 1L;
        Long joueurId = 1L;
        String login = "test";

        JoueurEntity joueur = new JoueurEntity();
        joueur.setId(joueurId);
        joueur.setPseudo(login);
        joueur.setEmail(login);
        joueur.setGenre(Genre.GARCON);
        joueur.setDateDeNaissance(LocalDate.now().minusYears(17));

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setWomenOnly(true);
        tournoi.setDateFinInscriptions(LocalDate.now().plusDays(1));
        tournoi.setCategories(Set.of(Categorie.JUNIOR));
        tournoi.setELOMin(null);
        tournoi.setELOMax(null);
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi.setNombreMaxJoueurs(10);

        when(tournoiRepository.existsById(tournoiId)).thenReturn(true);
        when(joueurRepository.findByPseudoOrEmail(eq(login), eq(login))).thenReturn(Optional.of(joueur));
        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findById(joueurId)).thenReturn(Optional.of(joueur));

        InscriptionTournoiException exception = assertThrows(InscriptionTournoiException.class,()-> tournoiService.inscriptionTournoi(1L,"test"));

        assertEquals("Impossible de s'inscrire au tournoi", exception.getMessage());
    }

    @Test
    void testInscriptionTournoiWhenTournoiDoesNotExist() {

        when(tournoiRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
            tournoiService.inscriptionTournoi(-99L, "Test")
        );

        assertEquals("Tournoi non trouvé", exception.getMessage());
    }

    @Test
    void testInscriptionTournoiWhenJoueurDoesNotExist() {

        when(tournoiRepository.existsById(anyLong())).thenReturn(true);
        when(joueurRepository.findByPseudoOrEmail(anyString(), anyString())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tournoiService.inscriptionTournoi(1L, "Test")
        );

        assertEquals("Joueur non trouvé", exception.getMessage());
    }

    @Test
    void canRegister_OK() {

        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(new ArrayList<>());
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(20);
        when(tournoiMock.getCategories()).thenReturn(Set.of(Categorie.JUNIOR));
        when(tournoiMock.isWomenOnly()).thenReturn(false);
        when(joueurMock.getDateDeNaissance()).thenReturn(LocalDate.now().minusYears(17));

        boolean result = tournoiService.canRegister(1L, 1L);

        assertTrue(result);
    }

    @Test
    void canRegister_tournoiPasTrouve(){
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tournoiService.canRegister(-99L, 1L)
        );

        assertEquals("Tournoi non trouvé", exception.getMessage());
    }

    @Test
    void canRegister_joueurPasTrouve(){
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(new TournoiEntity()));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tournoiService.canRegister(1L, -99L)
        );

        assertEquals("Joueur non trouvé", exception.getMessage());
    }

    @Test
    void canRegister_tournoiCommence(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);
        when(tournoiMock.getStatut()).thenReturn(Statut.EN_COURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));

        assertFalse(tournoiService.canRegister(1L,1L));
    }

    @Test
    void canRegister_tournoiTermine(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);
        when(tournoiMock.getStatut()).thenReturn(Statut.TERMINE);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));

        assertFalse(tournoiService.canRegister(1L, 1L));
    }

    @Test
    void canRegister_dateInscriptionDepassee(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);
        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().minusDays(1));

        assertFalse(tournoiService.canRegister(1L, 1L));
    }

    @Test
    void canRegister_tournoiJoueurMaxAtteint() {
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(List.of(new JoueurEntity(),new JoueurEntity()));
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(2);

        assertFalse(tournoiService.canRegister(1L, 1L));
    }

    @Test
    void canRegister_ELOJoueurTropGrand(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(List.of(new JoueurEntity(),new JoueurEntity()));
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(4);
        when(tournoiMock.getELOMax()).thenReturn(1000);
        when(joueurMock.getELO()).thenReturn(1500);

        assertFalse(tournoiService.canRegister(1L,1L));
    }

    @Test
    void canRegister_ELOJoueurTropPetit(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(List.of(new JoueurEntity(),new JoueurEntity()));
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(4);
        when(tournoiMock.getELOMax()).thenReturn(null);
        when(tournoiMock.getELOMin()).thenReturn(2000);
        when(joueurMock.getELO()).thenReturn(1500);

        assertFalse(tournoiService.canRegister(1L,1L));
    }

    @Test
    void canRegister_TournoiIsWomenOnly_JoueurGarcon(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(List.of(new JoueurEntity(),new JoueurEntity()));
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(4);
        when(tournoiMock.getELOMax()).thenReturn(null);
        when(tournoiMock.getELOMin()).thenReturn(null);
        when(tournoiMock.isWomenOnly()).thenReturn(true);
        when(joueurMock.getGenre()).thenReturn(Genre.GARCON);

        assertFalse(tournoiService.canRegister(1L,1L));
    }

    @Test
    void canRegister_JoueurPasBonneCategorie(){
        TournoiEntity tournoiMock = mock(TournoiEntity.class);
        JoueurEntity joueurMock = mock(JoueurEntity.class);

        when(tournoiMock.getStatut()).thenReturn(Statut.EN_ATTENTE_DE_JOUEURS);
        when(tournoiRepository.findById(anyLong())).thenReturn(Optional.of(tournoiMock));
        when(joueurRepository.findById(anyLong())).thenReturn(Optional.of(joueurMock));
        when(tournoiMock.getDateFinInscriptions()).thenReturn(LocalDate.now().plusDays(1));
        when(tournoiMock.getJoueurs()).thenReturn(List.of(new JoueurEntity(),new JoueurEntity()));
        when(tournoiMock.getNombreMaxJoueurs()).thenReturn(4);
        when(tournoiMock.getELOMax()).thenReturn(null);
        when(tournoiMock.getELOMin()).thenReturn(null);
        when(tournoiMock.getCategories()).thenReturn(Set.of(Categorie.SENIOR, Categorie.VETERAN));
        when(joueurMock.getDateDeNaissance()).thenReturn(LocalDate.now().minusYears(17));

        assertFalse(tournoiService.canRegister(1L,1L));
    }

    @Test
    void testIsRegistered_Ok() {
        TournoiEntity mockTournoi = mock(TournoiEntity.class);
        JoueurEntity mockJoueur = mock(JoueurEntity.class);
        when(mockTournoi.getJoueurs()).thenReturn(List.of(mockJoueur));
        when(tournoiRepository.findById(eq(1L))).thenReturn(Optional.of(mockTournoi));
        when(joueurRepository.findById(eq(1L))).thenReturn(Optional.of(mockJoueur));

       assertTrue(tournoiService.isRegistered(1L, 1L));
    }

    @Test
    void testIsRegistered_TournoiNotFound(){

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tournoiService.isRegistered(-99L,1L)
        );

        assertEquals("Tournoi non trouvé", exception.getMessage());
    }

    @Test
    void testIsRegistered_JoueurNotFound(){
        when(tournoiRepository.findById(eq(1L))).thenReturn(Optional.of(new TournoiEntity()));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                tournoiService.isRegistered(1L,-99L)
        );

        assertEquals("Joueur non trouvé", exception.getMessage());
    }

    @Test
    void testIsRegistered_JoueurDejaEnregistre() {
        TournoiEntity mockTournoi = mock(TournoiEntity.class);
        JoueurEntity mockJoueur = mock(JoueurEntity.class);

        when(mockTournoi.getJoueurs()).thenReturn(List.of(mockJoueur));

        when(tournoiRepository.findById(eq(1L))).thenReturn(Optional.of(mockTournoi));
        when(joueurRepository.findById(eq(1L))).thenReturn(Optional.of(mockJoueur));

        assertTrue(tournoiService.isRegistered(1L, 1L));

    }

    @Test
    void testDesinscriptionTournoi_OK() {

        Long tournoiId = 1L;
        String login = "test";

        JoueurEntity joueur = new JoueurEntity();
        joueur.setPseudo(login);
        joueur.setEmail(login);
        joueur.setId(1L);

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);
        tournoi.getJoueurs().add(joueur);

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findByPseudoOrEmail(login, login)).thenReturn(Optional.of(joueur));
        when(joueurRepository.findById(joueur.getId())).thenReturn(Optional.of(joueur));

        tournoiService.desinscriptionTournoi(tournoiId, login);

        assertFalse(tournoi.getJoueurs().contains(joueur));
        verify(tournoiRepository, times(1)).save(tournoi);
    }

    @Test
    void testDesinscriptionTournoi_TournoiNonExistant() {
        // Arrange
        Long tournoiId = 1L;
        String login = "test";

        // Ici nous supposons que le tournoi n'existe pas.
        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            tournoiService.desinscriptionTournoi(tournoiId, login);
        });
    }

    @Test
    void testDesinscriptionTournoi_TournoiNotFound() {
        Long tournoiId = 1L;
        String login = "test";

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tournoiService.desinscriptionTournoi(tournoiId, login);
        });

        assertEquals("Tournoi non trouvé", exception.getMessage());
    }

    @Test
    void testDesinscriptionTournoi_JoueurNotFound() {
        Long tournoiId = 1L;
        String login = "test";

        TournoiEntity tournoi = new TournoiEntity();

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findByPseudoOrEmail(login, login)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            tournoiService.desinscriptionTournoi(tournoiId, login);
        });
    }

    @Test
    void testDesinscriptionTournoi_TournoiNonEnAttente() {
        Long tournoiId = 1L;
        String login = "test";

        JoueurEntity joueur = new JoueurEntity();
        joueur.setPseudo(login);
        joueur.setEmail(login);
        joueur.setId(1L);

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setStatut(Statut.EN_COURS);
        tournoi.getJoueurs().add(joueur);

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findByPseudoOrEmail(login, login)).thenReturn(Optional.of(joueur));
        when(joueurRepository.findById(joueur.getId())).thenReturn(Optional.of(joueur));

        assertThrows(InscriptionTournoiException.class, () -> {
            tournoiService.desinscriptionTournoi(tournoiId, login);
        });
    }

    @Test
    void testDesinscriptionTournoi_JoueurPasInscrit() {
        Long tournoiId = 1L;
        String login = "test";

        JoueurEntity joueur = new JoueurEntity();
        joueur.setPseudo(login);
        joueur.setEmail(login);
        joueur.setId(1L);

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setStatut(Statut.EN_ATTENTE_DE_JOUEURS);

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(joueurRepository.findByPseudoOrEmail(login, login)).thenReturn(Optional.of(joueur));
        when(joueurRepository.findById(joueur.getId())).thenReturn(Optional.of(joueur));

        InscriptionTournoiException exception = assertThrows(InscriptionTournoiException.class, () -> {
            tournoiService.desinscriptionTournoi(tournoiId, login);
        });

        assertEquals("Impossible de se désinscrire du tournoi", exception.getMessage());
    }

    @Test
    void testDemarrerTournoi_OK() {
        Long tournoiId = 1L;
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setJoueurs(List.of(new JoueurEntity(), new JoueurEntity(), new JoueurEntity()));
        tournoi.setNombreMinJoueurs(2);
        tournoi.setDateFinInscriptions(LocalDate.now().minusDays(1));

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));

        tournoiService.demarrerTournoi(tournoiId);

        verify(tournoiRepository, times(1)).save(any(TournoiEntity.class));
    }

    @Test
    void testDemarrerTournoi_TournoiNonTrouve() {

        Long tournoiId = 1L;

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            tournoiService.demarrerTournoi(tournoiId);
        });

        assertEquals("Tournoi non trouvé", exception.getMessage());
        verify(tournoiRepository, times(1)).findById(tournoiId);
    }

    @Test
    void testDemarrerTournoi_NombreMinimumParticipantsNonAtteint() {
        Long tournoiId = 1L;

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setNombreMinJoueurs(2);
        tournoi.setJoueurs(List.of(new JoueurEntity()));

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));

        TournoiException exception = assertThrows(TournoiException.class, () -> {
            tournoiService.demarrerTournoi(tournoiId);
        });

        assertEquals("Nombre minimum de participants non atteint", exception.getMessage());
        verify(tournoiRepository, times(1)).findById(tournoiId);
    }

    @Test
    void testDemarrerTournoi_DateFinInscriptionsPasDepassee() {
        Long tournoiId = 1L;

        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setDateFinInscriptions(LocalDate.now().plusDays(1));

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));

        TournoiException exception = assertThrows(TournoiException.class, () -> {
            tournoiService.demarrerTournoi(tournoiId);
        });

        assertEquals("La date de fin des inscriptions n'est pas dépassée", exception.getMessage());
        verify(tournoiRepository, times(1)).findById(tournoiId);
    }

    @Test
    void testDemarrerTournoi_NombreImpairJoueurs() {
        Long tournoiId = 1L;
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setJoueurs(List.of(new JoueurEntity(), new JoueurEntity(), new JoueurEntity())); // Nombre impair de joueurs
        tournoi.setNombreMinJoueurs(2);
        tournoi.setDateFinInscriptions(LocalDate.now().minusDays(1));

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));

        tournoiService.demarrerTournoi(tournoiId);

        verify(tournoiRepository, times(1)).findById(tournoiId);
        verify(tournoiRepository, times(1)).save(any(TournoiEntity.class));
    }

    @Test
    void testDemarrerTournoi_NombrePairJoueurs() {
        Long tournoiId = 1L;
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setJoueurs(List.of(new JoueurEntity(), new JoueurEntity()));
        tournoi.setNombreMinJoueurs(2);
        tournoi.setDateFinInscriptions(LocalDate.now().minusDays(1));

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));

        tournoiService.demarrerTournoi(tournoiId);

        verify(tournoiRepository, times(1)).findById(tournoiId);
        verify(tournoiRepository, times(1)).save(any(TournoiEntity.class));
    }

    @Test
    void testModifierResultatRencontre() {
        Long rencontreId = 1L;
        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setId(rencontreId);
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setRonde(1);
        rencontre.setNumeroRonde(1);
        rencontre.setTournoi(tournoi);

        when(rencontreRepository.findById(rencontreId)).thenReturn(Optional.of(rencontre));

        tournoiService.modifierResultatRencontre(rencontreId, Resultat.BLANC);

        verify(rencontreRepository, times(1)).findById(rencontreId);
        verify(rencontreRepository, times(1)).save(any(RencontreEntity.class));
    }

    @Test
    void testModifierResultatRencontre_RencontreNonCourante() {
        Long rencontreId = 1L;
        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setId(rencontreId);
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setRonde(2);
        rencontre.setNumeroRonde(1);
        rencontre.setTournoi(tournoi);

        when(rencontreRepository.findById(rencontreId)).thenReturn(Optional.of(rencontre));

        assertThrows(RencontreException.class, () -> {
            tournoiService.modifierResultatRencontre(rencontreId, Resultat.BLANC);
        });

        verify(rencontreRepository, times(1)).findById(rencontreId);
        verifyNoMoreInteractions(rencontreRepository);
    }

    @Test
    void testPasserTourSuivant_OK() {
        Long tournoiId = 1L;
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setRonde(1);

        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setTournoi(tournoi);
        rencontre.setNumeroRonde(1);
        rencontre.setResultat(Resultat.BLANC);

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(rencontreRepository.findAll(any(Specification.class))).thenReturn(List.of(rencontre));

        tournoiService.passerTourSuivant(tournoiId);

        verify(tournoiRepository, times(1)).findById(tournoiId);
        verify(rencontreRepository, times(1)).findAll(any(Specification.class));
        verify(tournoiRepository, times(1)).save(any(TournoiEntity.class));
    }

    @Test
    void testPasserTourSuivant_TournoiNotFound(){
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,()->tournoiService.passerTourSuivant(-99L));

        assertEquals("Tournoi non trouvé", exception.getMessage());
    }

    @Test
    void testPasserTourSuivant_RencontresNonJouees() {
        Long tournoiId = 1L;
        TournoiEntity tournoi = new TournoiEntity();
        tournoi.setId(tournoiId);
        tournoi.setRonde(1);

        RencontreEntity rencontre = new RencontreEntity();
        rencontre.setTournoi(tournoi);
        rencontre.setNumeroRonde(1);
        rencontre.setResultat(null);

        when(tournoiRepository.findById(tournoiId)).thenReturn(Optional.of(tournoi));
        when(rencontreRepository.findAll(any(Specification.class))).thenReturn(List.of(rencontre));

        assertThrows(TournoiException.class, () -> {
            tournoiService.passerTourSuivant(tournoiId);
        });

        verify(tournoiRepository, times(1)).findById(tournoiId);
        verify(rencontreRepository, times(1)).findAll(any(Specification.class));
        verifyNoMoreInteractions(tournoiRepository);
    }

}
