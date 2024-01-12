package be.bstorm.formation.tournoiechecs.pl.validation;

import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinMaxJoueurValidator implements ConstraintValidator<MinMaxJoueur, TournoiForm> {

    @Override
    public boolean isValid(TournoiForm form, ConstraintValidatorContext constraintValidatorContext) {
        return form.nombreMaxJoueurs() >= form.nombreMinJoueurs();
    }
}
