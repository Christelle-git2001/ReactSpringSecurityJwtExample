package com.lacouf.rsbjwt.Exception;

public class OffrePasAVous extends RuntimeException {
    public OffrePasAVous() {
        super("Cette offre ne vous appartient pas.");
    }
}
