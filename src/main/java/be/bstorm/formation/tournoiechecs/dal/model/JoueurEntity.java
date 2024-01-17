package be.bstorm.formation.tournoiechecs.dal.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "joueur")
@Data
public class JoueurEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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
    private int eLO = 1200;
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.JOUEUR;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_"+role));
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return pseudo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
