package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<NotificationDTO>> recevoirNotifications(@PathVariable Long id) throws UtilisateurIntrouvableException, AucunRoleDefini {
        List<NotificationDTO> notificationDTOS = notificationService.recevoirNotifications(id);
        return ResponseEntity.ok(notificationDTOS);
    }

    @PutMapping("/lu/{id}")
    public ResponseEntity<NotificationDTO> mettreNotificationLue(@PathVariable Long id) throws NotificationNonTrouvee {
        NotificationDTO notificationDTO = notificationService.setNotificationLue(id);
        return ResponseEntity.ok(notificationDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationDTO> supprimerNotification(@PathVariable Long id) throws NotificationNonTrouvee {
        NotificationDTO notificationDTO = notificationService.supprimerNotification(id);
        return ResponseEntity.ok(notificationDTO);
    }
}
