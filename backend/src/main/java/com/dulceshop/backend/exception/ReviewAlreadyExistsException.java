package com.dulceshop.backend.exception;

public class ReviewAlreadyExistsException extends RuntimeException {

    public ReviewAlreadyExistsException(Long productId) {
        super("You have already reviewed product: " + productId);
    }
}
