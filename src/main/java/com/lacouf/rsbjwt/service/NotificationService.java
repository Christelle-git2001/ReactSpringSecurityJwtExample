package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.NotificationType;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.NouveauCvTeleverser;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserAppRepository userAppRepository;

    public NotificationService(NotificationRepository notificationRepository, UserAppRepository userAppRepository) {
        this.notificationRepository = notificationRepository;
        this.userAppRepository = userAppRepository;
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

    public List<NotificationDTO> recevoirNotifications(Long id) throws AucunRoleDefini, UtilisateurIntrouvableException {
        UserApp userApp = userAppRepository.findById(id)
                .orElseThrow(UtilisateurIntrouvableException::new);
        Role role = userApp.getRole();
        List<Notification> notifications = switch (role) {
            case GESTIONNAIRE -> notificationRepository.getNotificationByRole(role);
            case ETUDIANT, PROFESSEUR, EMPLOYEUR -> notificationRepository.getNotificationByDestinataire(userApp);
            case null -> throw new AucunRoleDefini();
        };
        return notifications.stream()
                .map(NotificationDTO::of)
                .toList();
    }
}
