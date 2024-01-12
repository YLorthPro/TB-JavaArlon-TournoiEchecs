package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.dal.repository.JoueurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JoueurRepository joueurRepository;

    public UserDetailsServiceImpl(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        return joueurRepository.findByPseudoOrEmail(identifiant, identifiant)
                .orElseThrow(() -> new UsernameNotFoundException("Login non trouvé"));
    }
}
