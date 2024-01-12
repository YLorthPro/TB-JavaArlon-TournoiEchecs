package be.bstorm.formation.tournoiechecs.pl.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinMaxELOValidator.class)
public @interface MinMaxELO {
    String message() default "L'ELO max ne peut pas être plus petit que l'ELO min";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
