package com.board.job.exception;

import com.board.job.model.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(HttpServletRequest request, ResponseStatusException ex) {
        return getErrorResponse(request, ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        String message = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return getErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({ConstraintViolationException.class, UserHaveNoPDF.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
        return getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, UserIsNotEmployer.class})
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, InvalidFile.class})
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception ex) {
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HttpServletRequest request, HttpStatusCode httpStatus, String message) {
        log.error("Exception raised = {} :: URL = {}", message, request.getRequestURL());
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(
                                LocalDateTime.now(),
                                httpStatus,
                                message,
                                request.getRequestURL().toString()
                        )
                );
    }

}
