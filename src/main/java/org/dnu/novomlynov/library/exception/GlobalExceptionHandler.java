package org.dnu.novomlynov.library.exception;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetails> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.error("Authentication error: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetails> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        log.error("Illegal state: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorDetails errorDetails = new ValidationErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                "Validation failed for request",
                request.getDescription(false).replace("uri=", ""),
                errors);
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("Constraint violation: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource",
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetails> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        log.error("Data integrity violation: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Data Integrity Violation",
                "A data integrity constraint was violated",
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: ", ex);
        ErrorDetails errorDetails = new ErrorDetails(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Data
    public static class ErrorDetails {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final String path;

        public ErrorDetails(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ValidationErrorDetails extends ErrorDetails {
        private final Map<String, String> violations;

        public ValidationErrorDetails(LocalDateTime timestamp, int status, String error, String message, String path,
                Map<String, String> violations) {
            super(timestamp, status, error, message, path);
            this.violations = violations;
        }
    }
}