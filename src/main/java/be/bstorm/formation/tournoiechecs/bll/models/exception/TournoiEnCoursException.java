package be.bstorm.formation.tournoiechecs.bll.models.exception;

public class TournoiEnCoursException extends RuntimeException{
    public TournoiEnCoursException() {
        super("Tournoi en cours!");
    }
}
