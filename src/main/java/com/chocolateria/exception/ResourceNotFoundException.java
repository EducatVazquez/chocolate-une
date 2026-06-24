package com.chocolateria.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super("%s con id=%d no fue encontrado".formatted(resource, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
