package com.dulceshop.backend.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(Long id) {
        super("Image not found: " + id);
    }
}