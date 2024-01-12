package be.bstorm.formation.tournoiechecs.dal.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "rencontre")
public class RencontreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int numeroRonde;
    private String resultat;

    @ManyToOne
    @JoinColumn(name = "tournoi_id")
    private TournoiEntity tournoi;

    @ManyToOne
    @JoinColumn(name = "joueur_blanc_id")
    private JoueurEntity joueurBlanc;

    @ManyToOne
    @JoinColumn(name = "joueur_noir_id")
    private JoueurEntity joueurNoir;
}
