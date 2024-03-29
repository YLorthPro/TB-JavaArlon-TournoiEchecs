package be.bstorm.formation.tournoiechecs.pl.model.dto;

import be.bstorm.formation.tournoiechecs.dal.model.RencontreEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Resultat;

public record Rencontre(
    Long id,
    Long tournoiId,
    String pseudoJoueurBlanc,
    String pseudoJoueurNoir,
    int ronde,
    Resultat resultat

) {
    public static Rencontre fromBll(RencontreEntity entity){
        return new Rencontre(
                entity.getId(),
                entity.getTournoi().getId(),
                entity.getJoueurBlanc().getPseudo(),
                entity.getJoueurNoir().getPseudo(),
                entity.getNumeroRonde(),
                entity.getResultat()
        );
    }
}
