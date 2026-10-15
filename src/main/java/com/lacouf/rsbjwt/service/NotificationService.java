package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Notification.NavigationPath;
import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.CvStatutModifieEvent;
import com.lacouf.rsbjwt.model.event.CvSupprimerSoNotification;
import com.lacouf.rsbjwt.model.event.NouveauCvTeleverserEvent;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    @Transactional
    public NotificationDTO handleNouveauCVTeleverser(NouveauCvTeleverserEvent event){
        CvEtudiant cv = event.cv();
        Etudiant etudiant = cv.getEtudiant();
        Notification notification = Notification.builder()
                .titre("notification.gestionnaire.televerserCV.title")
                .description("notification.gestionnaire.televerserCV.description")
                .role(Role.GESTIONNAIRE)
                .notificationType(NotificationType.CV_ATTENTE_VALIDATION)
                .dateCreaction(LocalDateTime.now())
                .urlCible(NavigationPath.GESTIONNAIRE_CV_DETAILS.build(String.valueOf(cv.getId())))
                .variables(Map.of(VariableMapKey.ETUDIANT_NOM,etudiant.getFirstName() + " " + etudiant.getLastName()))
                .build();
        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
    public NotificationDTO handleCvStatutModifieEvent(CvStatutModifieEvent event){
        Statut statut = event.statut();
        UserApp destinataire = event.destinataire();
        Notification notification = Notification.builder()
                .titre("notification.etudiant.validation.title")
                .description("notification.etudiant.validation.description")
                .notificationType(NotificationType.CV_STATUT_CHANGE)
                .dateCreaction(LocalDateTime.now())
                .urlCible(NavigationPath.ETUDIANT_CV.build())
                .role(Role.ETUDIANT)
                .destinataire(destinataire)
                .variables(Map.of(VariableMapKey.STATUT_FICHIER,statut.name()))
                .build();
        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
   public void handleCvSupprimerSoNotification(CvSupprimerSoNotification event){
        notificationRepository.deleteByUrlCible(event.navigationPath());
    }

    public List<NotificationDTO> recevoirNotifications(Long id) throws AucunRoleDefini, UtilisateurIntrouvableException {
        UserApp userApp = verifierEtRecupererUtilisateur(id);
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

    @Transactional
    public NotificationDTO setNotificationLue(Long id) throws NotificationNonTrouvee {
        Notification notification = notificationRepository.findById(id).orElseThrow(NotificationNonTrouvee::new);
        notification.setEstLu(true);
        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @Transactional
    public NotificationDTO supprimerNotification(Long id) throws NotificationNonTrouvee {
        NotificationDTO notificationDTO = NotificationDTO.of(notificationRepository.findById(id).orElseThrow(NotificationNonTrouvee::new));
        notificationRepository.deleteById(id);
        return notificationDTO;
    }

    public Long compterNotifications(Long id) throws AucunRoleDefini, UtilisateurIntrouvableException {
        UserApp userApp = verifierEtRecupererUtilisateur(id);
        Role role = userApp.getRole();
       return switch (role) {
            case GESTIONNAIRE -> notificationRepository.totalByRole(role);
            case ETUDIANT, PROFESSEUR, EMPLOYEUR -> notificationRepository.totalByUser(userApp);
            case null -> throw new AucunRoleDefini();
        };
    }

    private UserApp verifierEtRecupererUtilisateur(Long id) throws UtilisateurIntrouvableException {
        return userAppRepository.findById(id)
                .orElseThrow(UtilisateurIntrouvableException::new);
    }
}