package com.generatik.adbooking.adspace.handlers;


import com.generatik.adbooking.adspace.exceptions.InvalidEntityException;
import com.generatik.adbooking.adspace.exceptions.ResourceNotFoundException;
import com.generatik.adbooking.adspace.exceptions.ResourcesConflictException;
import com.generatik.adbooking.adspace.exceptions.UnavailableResourceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.generatik.adbooking")
public class ExceptionsHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<String> handleInvalidEntity(InvalidEntityException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnavailableResourceException.class)
    public ResponseEntity<String> handleUnavailableResource(UnavailableResourceException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourcesConflictException.class)
    public ResponseEntity<String> handleResourcesConflictException(ResourcesConflictException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
