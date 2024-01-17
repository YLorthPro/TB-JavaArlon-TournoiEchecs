package be.bstorm.formation.tournoiechecs.pl.controller;

import be.bstorm.formation.tournoiechecs.bll.models.exception.InscriptionTournoiException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.RencontreException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.TournoiEnCoursException;
import be.bstorm.formation.tournoiechecs.bll.models.exception.TournoiException;
import be.bstorm.formation.tournoiechecs.pl.model.dto.Error;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintDefinitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(ex.getMessage(), LocalDateTime.now(),req.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }

    @ExceptionHandler(ConstraintDefinitionException.class)
    public ResponseEntity<Error> handleConstraintDefinitionException(ConstraintDefinitionException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }


    @ExceptionHandler(InscriptionTournoiException.class)
    public ResponseEntity<Error> handleInscriptionTournoiException(InscriptionTournoiException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(),req.getRequestURI()));
    }

    @ExceptionHandler(RencontreException.class)
    public ResponseEntity<Error> handleRencontreException(RencontreException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(),req.getRequestURI()));
    }

    @ExceptionHandler(TournoiEnCoursException.class)
    public ResponseEntity<Error> handleTournoiEnCoursException(TournoiEnCoursException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(),req.getRequestURI()));
    }

    @ExceptionHandler(TournoiException.class)
    public ResponseEntity<Error> handleTournoiException(TournoiException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(ex.getMessage(), LocalDateTime.now(),req.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<Error> handleJWTVerificationException(JWTVerificationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Error> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(ex.getMessage(), LocalDateTime.now(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleInvalidForm(MethodArgumentNotValidException ex, HttpServletRequest req){

        List<String> errorMap = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(e -> {
            String message = e.getDefaultMessage();
            errorMap.add(message);
        });

        String errorsList = "{";

        for (String message: errorMap) {
            errorsList += message + ", ";
        }

        errorsList = errorsList.substring(0,errorsList.length()-2) + "}";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(errorsList,LocalDateTime.now(),req.getRequestURI()));

    }


}
