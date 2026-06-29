package com.scalebook.scalebook_backend.exception;

public class ResourceAlreadyBookedException extends RuntimeException {
    public ResourceAlreadyBookedException(String message) {
        super(message);
    }
}