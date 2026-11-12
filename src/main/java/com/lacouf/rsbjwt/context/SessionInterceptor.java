package com.lacouf.rsbjwt.context;

import com.lacouf.rsbjwt.model.Enum.Saison;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private final SessionContext sessionContext;

    public SessionInterceptor(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String headerSession = request.getHeader("X-Session-Cible"); // Ex: "AUTOMNE-2025"

        if (headerSession != null && headerSession.contains("-")) {
            String[] parts = headerSession.split("-");
            try {
                Saison saison = Saison.valueOf(parts[0].toUpperCase());
                int annee = Integer.parseInt(parts[1]);
                sessionContext.setSessionCible(saison, annee);
            } catch (Exception e) {
                // Format invalide ignoré, on garde la session par défaut
            }
        }
        return true;
    }
}
