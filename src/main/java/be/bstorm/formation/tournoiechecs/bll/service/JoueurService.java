package be.bstorm.formation.tournoiechecs.bll.service;

import be.bstorm.formation.tournoiechecs.pl.model.dto.Auth;
import be.bstorm.formation.tournoiechecs.pl.model.form.JoueurForm;
import be.bstorm.formation.tournoiechecs.pl.model.form.LoginForm;

public interface JoueurService {
    void inscription (JoueurForm joueur);
    Auth login(LoginForm form);
}
