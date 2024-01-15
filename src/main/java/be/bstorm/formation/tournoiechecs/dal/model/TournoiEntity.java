package be.bstorm.formation.tournoiechecs.dal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "tournoi")
public class TournoiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String lieu;
    private int nombreMinJoueurs;
    private int nombreMaxJoueurs;
    private Integer eLOMin;
    private Integer eLOMax;
    @Enumerated(EnumType.STRING)
    private Set<Categorie> categories;
    @Enumerated(EnumType.STRING)
    private Statut statut;
    private int ronde;
    private boolean womenOnly;
    private LocalDate dateFinInscriptions;
    private LocalDate dateCreation;
    private LocalDate dateModification;

    @ManyToMany
    @JoinTable(name = "Tournoi_joueur",
            joinColumns = @JoinColumn(name = "tournoi_id"),
            inverseJoinColumns = @JoinColumn(name = "joueur_id"))
    private List<JoueurEntity> joueurs = new ArrayList<>();


    @OneToMany(mappedBy = "tournoi")
    private List<RencontreEntity> rencontreEntity;

}
