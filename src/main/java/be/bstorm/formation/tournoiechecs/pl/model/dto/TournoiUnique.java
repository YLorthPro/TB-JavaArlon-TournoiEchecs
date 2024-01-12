package be.bstorm.formation.tournoiechecs.pl.model.dto;

import be.bstorm.formation.tournoiechecs.dal.model.Categorie;
import be.bstorm.formation.tournoiechecs.dal.model.RencontreEntity;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record TournoiUnique(
    Long id,
    String nom,
    String lieu,
    int nombreJoueursInscrits,
    int nombreMinJoueurs,
    int nombreMaxJoueurs,
    Integer eLOMin,
    Integer eLOMax,
    Set<Categorie> categories,
    Statut statut,
    boolean womenOnly,
    LocalDate dateFinInscriptions,
    int rondeCourante,
    List<Rencontre> recontresActuelles
){
    public static TournoiUnique fromBll(TournoiEntity entity){
        return new TournoiUnique(
                entity.getId(),
                entity.getNom(),
                entity.getLieu(),
                entity.getJoueurs().size(),
                entity.getNombreMinJoueurs(),
                entity.getNombreMaxJoueurs(),
                entity.getELOMin(),
                entity.getELOMax(),
                entity.getCategories(),
                entity.getStatut(),
                entity.isWomenOnly(),
                entity.getDateFinInscriptions(),
                entity.getRonde(),
                entity.getRencontreEntity().stream().map(Rencontre::fromBll).toList()
        );
    }
}
