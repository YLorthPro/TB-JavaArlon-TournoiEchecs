package be.bstorm.formation.tournoiechecs.pl.model.form;

import be.bstorm.formation.tournoiechecs.dal.model.Categorie;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.pl.validation.DateFinInscription;
import be.bstorm.formation.tournoiechecs.pl.validation.MinMaxELO;
import be.bstorm.formation.tournoiechecs.pl.validation.MinMaxJoueur;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

@MinMaxJoueur
@MinMaxELO
@DateFinInscription
public record TournoiForm(
    @NotBlank
    String nom,
    @NotBlank
    String lieu,
    @Min(2)
    @Max(32)
    int nombreMinJoueurs,
    @Min(2)
    @Max(32)
    int nombreMaxJoueurs,
    @Min(0)
    @Max(3000)
    Integer eLOMin,
    @Min(0)
    @Max(3000)
    Integer eLOMax,
    @NotEmpty
    Set<Categorie> categories,
    @NotNull
    Statut statut,
    boolean womenOnly,
    LocalDate dateFinInscriptions
) {
}
