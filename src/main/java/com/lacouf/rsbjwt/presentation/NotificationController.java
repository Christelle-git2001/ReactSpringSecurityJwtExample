package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notification/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotification(@PathVariable Long id) throws UtilisateurIntrouvableException, AucunRoleDefini {
        List<NotificationDTO> notificationDTOS = notificationService.recevoirNotifications(id);
        return ResponseEntity.ok(notificationDTOS);
    }
}
