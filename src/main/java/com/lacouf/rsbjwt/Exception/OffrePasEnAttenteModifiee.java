package com.lacouf.rsbjwt.Exception;

public class OffrePasEnAttenteModifiee extends RuntimeException {
    public OffrePasEnAttenteModifiee() {
        super("Une offre qui n'est pas en attente ne peut pas être modifiée");
    }
}
