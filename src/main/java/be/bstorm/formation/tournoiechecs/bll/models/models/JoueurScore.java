package be.bstorm.formation.tournoiechecs.bll.models.models;

import lombok.Data;

@Data
public class JoueurScore {
    private String nom;
    private int rencontreJouees;
    private int victoires;
    private int defaites;
    private int egalite;
    private double score;
}
