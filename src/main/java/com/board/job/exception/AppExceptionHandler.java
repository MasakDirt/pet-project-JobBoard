package com.board.job.exception;

import com.board.job.model.dto.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ModelAndView handleResponseStatusException(HttpServletRequest request, ResponseStatusException ex) {
        return getErrorResponse(request, ex.getStatusCode(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        String message = ex.getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return getErrorResponse(request, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({ConstraintViolationException.class, UserHaveNoPDF.class, IllegalArgumentException.class})
    public ModelAndView handleBadRequestExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ModelAndView handleBadCredentialsException(HttpServletRequest request, BadCredentialsException ex) {
        return getErrorResponse(request, HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({AccessDeniedException.class, UserIsNotEmployer.class})
    public ModelAndView handleForbiddenExceptions(HttpServletRequest request, RuntimeException ex) {
        return getErrorResponse(request, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleNotFoundExceptions(HttpServletRequest request, EntityNotFoundException ex) {
        return getErrorResponse(request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception ex) {
        return getErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ModelAndView getErrorResponse(HttpServletRequest request, HttpStatusCode httpStatus, String message) {
        log.error("Exception raised = {} :: URL = {}", message, request.getRequestURL());

        ModelMap map = new ModelMap();
        map.addAttribute("errorResponse", new ErrorResponse(
                LocalDateTime.now(),
                httpStatus,
                message,
                request.getRequestURL().toString()
        ));
        map.addAttribute("formatter", DateTimeFormatter.ofPattern("h:mm a"));
        return new ModelAndView("errors/error", map);
    }

}
