package be.bstorm.formation.tournoiechecs.pl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinMaxJoueurValidator.class)
public @interface MinMaxJoueur {
    String message() default "Le nombre maximum de joueurs ne peut être plus petit que le nombre minimum de joueurs";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
