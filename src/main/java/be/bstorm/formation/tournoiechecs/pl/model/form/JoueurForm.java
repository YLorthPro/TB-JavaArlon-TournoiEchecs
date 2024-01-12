package be.bstorm.formation.tournoiechecs.pl.model.form;

import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.Role;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record JoueurForm(
        String pseudo,
        String email ,
        LocalDate dateDeNaissance,
        Genre genre,
        @Min(0)
        @Max(3000)
        Integer eLO,
        Role role
) {
}
