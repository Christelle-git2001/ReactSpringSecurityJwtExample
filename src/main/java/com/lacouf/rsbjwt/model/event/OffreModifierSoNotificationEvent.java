package com.lacouf.rsbjwt.model.event;


import com.lacouf.rsbjwt.model.OffreDeStage;

public record OffreModifierSoNotificationEvent(OffreDeStage ancienneOffre, OffreDeStage nouvelleOffre) {
}
