package com.lacouf.rsbjwt.context;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.service.dto.SessionDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class SessionContext {
    private Saison saisonCible;
    private Integer anneeCible;

    public void setSessionCible(Saison saison, Integer annee) {
        this.saisonCible = saison;
        this.anneeCible = annee;
    }

    public boolean hasSessionCible() {
        return saisonCible != null && anneeCible != null;
    }

    public SessionDTO getSessionCible() {
        return new SessionDTO(saisonCible, anneeCible);
    }
}