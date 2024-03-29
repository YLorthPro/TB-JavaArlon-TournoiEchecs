package be.bstorm.formation.tournoiechecs.pl.controller;

import be.bstorm.formation.tournoiechecs.bll.models.models.JoueurScore;
import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.Resultat;
import be.bstorm.formation.tournoiechecs.pl.model.dto.TournoiListe;
import be.bstorm.formation.tournoiechecs.pl.model.dto.TournoiUnique;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournoi")
@CrossOrigin("*")
public class TournoiController {

    private final TournoiService tournoiService;

    public TournoiController(TournoiService tournoiService) {
        this.tournoiService = tournoiService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/creation")
    @ResponseStatus(HttpStatus.CREATED)
    public void creationTournoi(@RequestBody @Valid TournoiForm form) {
        tournoiService.creationTournoi(form);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id:[0-9]+}")
    public void suppressionTournoi(@PathVariable Long id){
        tournoiService.suppressionTournoi(id);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/top10")
    public ResponseEntity<List<TournoiListe>> top10() {
        return ResponseEntity.ok(tournoiService.top10().stream().map(TournoiListe::fromBll).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/recherche")
    public ResponseEntity<Page<TournoiListe>> recherche(@RequestBody TournoiSearchForm form, Pageable pageable) {
        return ResponseEntity.ok(tournoiService.recherche(form, pageable).map(TournoiListe::fromBll));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<TournoiUnique> getTournoiById(@PathVariable Long id) {
        return ResponseEntity.ok(tournoiService.getTournoiById(id).map(TournoiUnique::fromBll).orElseThrow(()->new EntityNotFoundException("Tournoi pas trouvé")));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/inscription/{tournoiId:[0-9]+}")
    public void inscriptionTournoi(@PathVariable Long tournoiId, Authentication authentication) {
        tournoiService.inscriptionTournoi(tournoiId, authentication.getName());
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/desinscription/{tournoiId:[0-9]+}")
    public void desinscriptionTournoi(@PathVariable Long tournoiId, Authentication authentication) {
        tournoiService.desinscriptionTournoi(tournoiId, authentication.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/demarrer/{id:[0-9]+}")
    public void demarrerTournoi(@PathVariable Long id) {
        tournoiService.demarrerTournoi(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/rencontre/{rencontreId:[0-9]+}/resultat")
    public void modifierResultatRencontre(@PathVariable Long rencontreId, @RequestBody Resultat resultat) {
        tournoiService.modifierResultatRencontre(rencontreId, resultat);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/tourSuivant/{tournoiId:[0-9]+}")
    public void passerTourSuivant(@PathVariable Long tournoiId) {
        tournoiService.passerTourSuivant(tournoiId);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{tournoiId:[0-9]+}/tableauScores/{ronde:[0-9]+}")
    public ResponseEntity<List<JoueurScore>> afficherTableauScores(@PathVariable Long tournoiId, @PathVariable int ronde) {
        return ResponseEntity.ok(tournoiService.afficherTableauScores(tournoiId, ronde));
    }
}
