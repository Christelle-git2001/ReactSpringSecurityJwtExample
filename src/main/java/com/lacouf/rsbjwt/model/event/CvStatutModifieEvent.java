package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.UserApp;
import java.time.LocalDateTime;

public record CvStatutModifieEvent(UserApp destinataire, Statut statut, LocalDateTime dateDepot) {
}
