package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.service.dto.SessionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SessionService {

    public SessionDTO getSessionActuelle() {
        return getSession(LocalDate.now());
    }

    public SessionDTO getSession(LocalDate date) {
        int mois = date.getMonthValue();
        int jour = date.getDayOfMonth();
        int annee = date.getYear();

        if ((mois == 8 && jour >= 20) || mois >= 9) {
            return new SessionDTO(Saison.AUTOMNE, annee);
        }
        if (mois <= 5 || (mois == 6 && jour <= 10)) {
            return new SessionDTO(Saison.HIVER, annee);
        }
        return new SessionDTO(Saison.AUTOMNE, annee); // Transition été
    }

    public SessionDTO assignerSession(LocalDate date) {
        return getSession(date);
    }
}
