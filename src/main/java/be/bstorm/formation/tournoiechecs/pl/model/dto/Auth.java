package be.bstorm.formation.tournoiechecs.pl.model.dto;

import be.bstorm.formation.tournoiechecs.dal.model.Role;

public record Auth(String token, Role role, String username) {
}
