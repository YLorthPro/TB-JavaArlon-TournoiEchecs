package be.bstorm.formation.tournoiechecs.pl.controller;

import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.pl.model.dto.TournoiListe;
import be.bstorm.formation.tournoiechecs.pl.model.dto.TournoiUnique;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournoi")
public class TournoiController {

    private final TournoiService tournoiService;

    public TournoiController(TournoiService tournoiService) {
        this.tournoiService = tournoiService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/creation")
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
    @GetMapping("/recherche")
    public ResponseEntity<Page<TournoiListe>> recherche(@RequestBody TournoiSearchForm form, Pageable pageable) {
        return ResponseEntity.ok(tournoiService.recherche(form, pageable).map(TournoiListe::fromBll));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<TournoiUnique> getTournoiById(@PathVariable Long id) {
        return ResponseEntity.ok(tournoiService.getTournoiById(id).map(TournoiUnique::fromBll).orElseThrow(()->new EntityNotFoundException("Tournoi pas trouvé")));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/inscription/{tournoiId:[0-9]+}")
    public void inscriptionTournoi(@PathVariable Long tournoiId, Authentication authentication) {
        tournoiService.inscriptionTournoi(tournoiId, authentication.getName());
    }
}
