package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.NotificationType;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.NouveauCvTeleverser;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    public void handleNouveauCVTeleverser(NouveauCvTeleverser event){
        CvEtudiant cv = event.cv();
        Etudiant etudiant = cv.getEtudiant();
        Notification notification = Notification.builder()
                .titre("notification.gestionnaire.televerserCV.title")
                .description("notification.gestionnaire.televerserCV.description")
                .role(Role.GESTIONNAIRE)
                .notificationType(NotificationType.CVATTENTEVALIDATION)
                .dateCreaction(LocalDateTime.now())
                .urlCible("/cv/" + cv.getId() + "/download")
                .variables(Map.of("nomEtudiant",etudiant.getFirstName() + " " + etudiant.getLastName()))
                .build();
        notificationRepository.save(notification);
    }
}
