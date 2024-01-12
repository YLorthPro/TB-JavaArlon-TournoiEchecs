package be.bstorm.formation.tournoiechecs.pl.validation;

import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateFinInscriptionValidator implements ConstraintValidator<DateFinInscription, TournoiForm> {

    @Override
    public boolean isValid(TournoiForm tournoiForm, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate expectedDate = LocalDate.now().plusDays(tournoiForm.nombreMinJoueurs());

        return tournoiForm.dateFinInscriptions().isAfter(expectedDate);
    }
}
