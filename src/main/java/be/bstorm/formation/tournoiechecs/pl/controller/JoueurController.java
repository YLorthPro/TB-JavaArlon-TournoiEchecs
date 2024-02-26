package be.bstorm.formation.tournoiechecs.pl.controller;

import be.bstorm.formation.tournoiechecs.bll.service.JoueurService;
import be.bstorm.formation.tournoiechecs.pl.model.dto.Auth;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.LoginForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/joueur")
public class JoueurController {

    private final JoueurService joueurService;

    public JoueurController(JoueurService joueurService) {
        this.joueurService = joueurService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/inscription")
    @ResponseStatus(HttpStatus.CREATED)
    public void inscription(@RequestBody @Valid JoueurForm joueur) {
        joueurService.inscription(joueur);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<Auth> login(@RequestBody LoginForm form) {
        return ResponseEntity.ok(joueurService.login(form));
    }
}
