package be.bstorm.formation.tournoiechecs.dal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "joueur")
@Data
public class JoueurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String pseudo;
    @Column(unique = true)
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;
    @Column(name = "date_de_naissance")
    private LocalDate dateDeNaissance;
    @Enumerated(value = EnumType.STRING)
    private Genre genre;
    private int eLO;
    @Enumerated(value = EnumType.STRING)
    private Role role;

}
