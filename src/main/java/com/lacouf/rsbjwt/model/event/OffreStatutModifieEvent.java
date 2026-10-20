package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.model.UserApp;

public record OffreStatutModifieEvent(UserApp destinataire, Statut statut, OffreDeStage offre) {
}