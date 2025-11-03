package be.salon.coiffurereservation.controller;

import be.salon.coiffurereservation.dto.ErrorResponse;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions pour les contrôleurs REST.
 *
 * <p>
 * Cette classe centralise la conversion des exceptions applicatives en réponses HTTP
 * normalisées à l’aide du DTO {@link ErrorResponse}. Elle assure également la journalisation
 * des erreurs (warnings pour les erreurs fonctionnelles, errors pour les exceptions inattendues).
 * </p>
 *
 * <p>
 * Les principales erreurs gérées :
 * <ul>
 *     <li>{@link IllegalArgumentException} → 400 Bad Request</li>
 *     <li>{@link MethodArgumentNotValidException} → 400 Bad Request (détails de validation)</li>
 *     <li>{@link AuthenticationException} / {@link BadCredentialsException} → 401 Unauthorized</li>
 *     <li>{@link AccessDeniedException} → 403 Forbidden</li>
 *     <li>{@link StripeException} → 400 Payment Error</li>
 *     <li>{@link Exception} (fallback) → 500 Internal Server Error</li>
 * </ul>
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Gère les erreurs de type {@link IllegalArgumentException}.
     *
     * @param ex      exception levée.
     * @param request requête HTTP associée.
     * @return réponse HTTP 400 contenant le message d’erreur.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("IllegalArgumentException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gère les erreurs de validation des paramètres d’entrée (Bean Validation).
     *
     * @param ex      exception de validation regroupant les erreurs sur les champs.
     * @param request requête HTTP associée.
     * @return réponse HTTP 400 contenant la liste des erreurs de validation par champ.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Des erreurs de validation ont été détectées")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gère les erreurs d’authentification génériques (jeton invalide, utilisateur non authentifié…).
     *
     * @param ex      exception d’authentification.
     * @param request requête HTTP associée.
     * @return réponse HTTP 401 avec un message standardisé.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("AuthenticationException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Incorrect email or password",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Gère l’erreur d’identifiants invalides (email/mot de passes incorrects).
     *
     * @param ex      exception BadCredentials.
     * @param request requête HTTP associée.
     * @return réponse HTTP 401 avec un message standardisé.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("BadCredentialsException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Incorrect email or password",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Gère les erreurs d’autorisation (droits insuffisants).
     *
     * @param ex      exception AccessDenied.
     * @param request requête HTTP associée.
     * @return réponse HTTP 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("AccessDeniedException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access denied",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Gère les erreurs en provenance de Stripe lors du traitement des paiements.
     *
     * @param ex      exception Stripe.
     * @param request requête HTTP associée.
     * @return réponse HTTP 400 avec un message détaillant l’erreur Stripe.
     */
    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ErrorResponse> handleStripeException(
            StripeException ex,
            HttpServletRequest request) {

        log.error("StripeException: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Payment Error",
                "Payment processing error: " + ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gestion de secours pour toute exception non prévue.
     *
     * @param ex      exception inattendue.
     * @param request requête HTTP associée.
     * @return réponse HTTP 500 avec un message générique.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error", ex);
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
