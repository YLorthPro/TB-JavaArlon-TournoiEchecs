package be.bstorm.formation.tournoiechecs.dal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class TournoiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String lieu;
    private int nombreMinJoueurs;
    private int nombreMaxJoueurs;
    private int eLOMin;
    private int eLOMax;
    @Enumerated(EnumType.STRING)
    private Set<Categorie> categories;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    private int ronde;
    private boolean womenOnly;
    private LocalDate dateFinInscriptions;
    private LocalDate dateCreation;
    private LocalDate dateModification;
}
