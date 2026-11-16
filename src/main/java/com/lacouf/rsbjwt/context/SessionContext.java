package com.lacouf.rsbjwt.context;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.service.dto.SessionDTO;
import org.springframework.stereotype.Component;

@Component
public class SessionContext {
    private static final ThreadLocal<SessionDTO> sessionCibleHolder = new ThreadLocal<>();

    public void setSessionCible(Saison saison, Integer annee) {
        if (saison != null && annee != null) {
            sessionCibleHolder.set(new SessionDTO(saison, annee));
        } else {
            sessionCibleHolder.remove();
        }
    }

    public boolean hasSessionCible() {
        return sessionCibleHolder.get() != null;
    }

    public SessionDTO getSessionCible() {
        return sessionCibleHolder.get();
    }

    public void clear() {
        sessionCibleHolder.remove();
    }
}