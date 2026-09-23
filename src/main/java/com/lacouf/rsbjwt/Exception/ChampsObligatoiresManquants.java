package com.lacouf.rsbjwt.Exception;

public class ChampsObligatoiresManquants extends Exception {
    public ChampsObligatoiresManquants() {
        super("Un champs obligatoires est manquant");
    }
}
