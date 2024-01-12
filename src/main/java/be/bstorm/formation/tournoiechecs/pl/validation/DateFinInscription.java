package be.bstorm.formation.tournoiechecs.pl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateFinInscriptionValidator.class)
public @interface DateFinInscription {
    String message() default "La date de fin d'inscription doit être supérieure à la date actuelle plus le nombre minimum de joueurs";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
