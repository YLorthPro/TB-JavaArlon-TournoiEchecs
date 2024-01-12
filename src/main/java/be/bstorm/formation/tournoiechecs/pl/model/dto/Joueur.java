package be.bstorm.formation.tournoiechecs.pl.model.dto;

import be.bstorm.formation.tournoiechecs.dal.model.Genre;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;

import java.time.LocalDate;

public record Joueur(
        String pseudo,
        String email,
        LocalDate dateDeNaissance,
        int eLO,
        Genre genre
) {
    public static Joueur fromBll(JoueurEntity entity){
        return new Joueur(
                entity.getPseudo(),
                entity.getEmail(),
                entity.getDateDeNaissance(),
                entity.getELO(),
                entity.getGenre()
        );
    }
}
