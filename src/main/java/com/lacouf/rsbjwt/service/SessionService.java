package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.context.SessionContext;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.repository.InscriptionRepository;
import com.lacouf.rsbjwt.service.dto.SessionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class SessionService {

    private final SessionContext sessionContext;
    private final InscriptionRepository inscriptionRepository;

    public SessionService(SessionContext sessionContext, InscriptionRepository inscriptionRepository) {
        this.sessionContext = sessionContext;
        this.inscriptionRepository = inscriptionRepository;
    }

    public record SessionPeriode(LocalDateTime debut, LocalDateTime fin) {}

    public SessionDTO getSessionActuelle() {
        if (sessionContext.hasSessionCible()) {
            return sessionContext.getSessionCible();
        }
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

    public SessionPeriode getPeriodeSession(Saison saison, int annee) {
        if (saison == Saison.AUTOMNE) {
            return new SessionPeriode(
                    LocalDateTime.of(annee, 8, 20, 0, 0),
                    LocalDateTime.of(annee, 12, 31, 23, 59, 59)
            );
        } else {
            return new SessionPeriode(
                    LocalDateTime.of(annee, 1, 1, 0, 0),
                    LocalDateTime.of(annee, 8, 19, 23, 59, 59)
            );
        }
    }

    public <T> List<T> filtrerParSession(List<T> elements, Saison saison, int annee, Function<T, LocalDateTime> dateExtractor) {
        SessionPeriode periode = getPeriodeSession(saison, annee);

        return elements.stream()
                .filter(el -> {
                    LocalDateTime date = dateExtractor.apply(el);
                    return date != null && !date.isBefore(periode.debut()) && !date.isAfter(periode.fin());
                })
                .toList();
    }

    public <T> List<T> filtrerParSessionActuelle(List<T> elements, Function<T, LocalDateTime> dateExtractor) {
        SessionDTO sessionActuelle = getSessionActuelle();
        return filtrerParSession(elements, sessionActuelle.saison(), sessionActuelle.annee(), dateExtractor);
    }

    public <T extends UserApp> List<T> filtrerUtilisateursParSession(List<T> utilisateurs, Saison saison, int annee) {
        return utilisateurs.stream()
                .filter(user -> user.getInscriptions().stream()
                        .anyMatch(i -> i.getSaison() == saison && i.getAnnee() == annee))
                .toList();
    }

    public <T extends UserApp> List<T> filtrerUtilisateursParSessionActuelle(List<T> utilisateurs) {
        SessionDTO sessionActuelle = getSessionActuelle();
        return filtrerUtilisateursParSession(utilisateurs, sessionActuelle.saison(), sessionActuelle.annee());
    }

    public List<SessionDTO> getSessionsDisponibles() {
        return inscriptionRepository.findSessionsDisponibles().stream()
                .map(row -> new SessionDTO((Saison) row[0], (Integer) row[1]))
                .toList();
    }
}