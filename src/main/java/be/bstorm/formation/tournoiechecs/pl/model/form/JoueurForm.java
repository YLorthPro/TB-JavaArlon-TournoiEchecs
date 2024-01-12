package be.bstorm.formation.tournoiechecs.pl.model.form;

import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record JoueurForm(
        @NotBlank
        String pseudo,
        @NotBlank
        @Email
        String email ,
        @Past
        LocalDate dateDeNaissance,
        @NotNull
        Genre genre,
        @Min(0)
        @Max(3000)
        Integer eLO,
        @NotNull
        Role role
) {
}
