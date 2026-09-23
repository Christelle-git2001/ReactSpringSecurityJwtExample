package com.lacouf.rsbjwt.Exception;

public class ChampsObligatoiresManquants extends Exception {
    public ChampsObligatoiresManquants(String champs) {
        super("Le champs " + champs + "est manquant");
    }
}
