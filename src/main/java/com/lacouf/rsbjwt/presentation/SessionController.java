package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.SessionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/session")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("disponibles")
    public ResponseEntity<List<SessionDTO>> obtenirSessions() {
        return ResponseEntity.ok(sessionService.getSessionsDisponibles());
    }

    @GetMapping("/actuelle")
    public ResponseEntity<SessionDTO> obtenirSessionActuelle() {
        return ResponseEntity.ok(sessionService.getSessionActuelle());
    }
}
