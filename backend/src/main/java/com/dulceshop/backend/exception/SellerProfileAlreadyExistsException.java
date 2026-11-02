package com.dulceshop.backend.exception;

public class SellerProfileAlreadyExistsException extends RuntimeException {

    public SellerProfileAlreadyExistsException(Long userId) {
        super("Cet utilisateur possède déjà un profil vendeur : " + userId);
    }
}