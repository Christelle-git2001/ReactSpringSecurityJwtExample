package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.Session;
import com.lacouf.rsbjwt.service.dto.SessionInfoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SessionService {

    public SessionInfoDTO getSessionActuelle() {
        return getSessionFromDate(LocalDate.now());
    }

    public SessionInfoDTO getSessionFromDate(LocalDate date) {
        int mois = date.getMonthValue();
        int jour = date.getDayOfMonth();
        int annee = date.getYear();

        // 20 août au 31 décembre
        if ((mois == 8 && jour >= 20) || mois >= 9) {
            return new SessionInfoDTO(Session.AUTOMNE, annee);
        }

        //janvier - mai
        if (mois <= 5) {
            return new SessionInfoDTO(Session.HIVER, annee);
        }

        //1er juin au 19 août, je ne sais pas si on pourrait garder le mois sans le jour??Hein Graciela?
        return new SessionInfoDTO(Session.ETE, annee);
    }

    public SessionInfoDTO getSessionStageAVenir() {
        return getSessionStageAVenir(LocalDate.now());
    }

    public SessionInfoDTO getSessionStageAVenir(LocalDate date) {
        SessionInfoDTO actuelle = getSessionFromDate(date);

        return switch (actuelle.session()) {
            case AUTOMNE ->
                    new SessionInfoDTO(Session.HIVER, actuelle.annee() + 1);

            case HIVER ->
                    new SessionInfoDTO(Session.ETE, actuelle.annee());

            case ETE ->
                    new SessionInfoDTO(Session.AUTOMNE, actuelle.annee());
        };
    }
}

