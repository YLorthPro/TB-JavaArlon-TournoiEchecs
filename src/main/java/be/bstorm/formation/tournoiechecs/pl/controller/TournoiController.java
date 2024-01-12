package be.bstorm.formation.tournoiechecs.pl.controller;

import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tournoi")
public class TournoiController {

    private final TournoiService tournoiService;

    public TournoiController(TournoiService tournoiService) {
        this.tournoiService = tournoiService;
    }

    @PostMapping("/creation")
    public void creationTournoi(@RequestBody @Valid TournoiForm form) {
        tournoiService.creationTournoi(form);
    }
}
