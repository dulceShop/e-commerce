package com.dulceshop.backend.exception;

public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(Long id) {
        super("Profil vendeur introuvable : " + id);
    }
}