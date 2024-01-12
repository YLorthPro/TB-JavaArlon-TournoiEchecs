package be.bstorm.formation.tournoiechecs.pl.model.form;

import be.bstorm.formation.tournoiechecs.dal.model.Categorie;
import be.bstorm.formation.tournoiechecs.dal.model.Statut;

import java.util.List;

public record TournoiSearchForm(
        String nom,
        Statut statut,
        List<Categorie> categories
        ) {
}
