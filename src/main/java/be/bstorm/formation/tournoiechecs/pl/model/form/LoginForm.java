package be.bstorm.formation.tournoiechecs.pl.model.form;

import lombok.Builder;
import lombok.Data;

public record LoginForm (
    String identifiant,
    String motDePasse
){
}
