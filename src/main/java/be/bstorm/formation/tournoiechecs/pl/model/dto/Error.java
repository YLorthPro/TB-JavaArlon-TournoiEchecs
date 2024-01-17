package be.bstorm.formation.tournoiechecs.pl.model.dto;

import java.time.LocalDateTime;

public record Error (
    String message,
    LocalDateTime requestMadeAt,
    String URI
){
}
