package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.OffreIntrouvableException;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.*;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
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
    private final EtudiantRepository etudiantRepository;

    public NotificationService(NotificationRepository notificationRepository, UserAppRepository userAppRepository,
                               EtudiantRepository etudiantRepository) {
        this.notificationRepository = notificationRepository;
        this.userAppRepository = userAppRepository;
        this.etudiantRepository = etudiantRepository;
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
                .urlCible(ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build((String.valueOf(cv.getId()))))
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
                .titre("notification.etudiant.validation.title_" + statut.name().toLowerCase())
                .description("notification.etudiant.validation.description_" + statut.name().toLowerCase())
                .notificationType(NotificationType.CV_STATUT_CHANGE)
                .dateCreaction(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .urlCible("")
                .destinataire(destinataire)
                .variables(Map.of(VariableMapKey.STATUT_FICHIER,statut.name()))
                .build();
        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
    public NotificationDTO handleOffreStatutModifie(OffreStatutModifieEvent event) {
        OffreDeStage offre = event.offre();
        UserApp destinataire = event.destinataire();

        Notification notification = Notification.builder()
                .titre("notification.employeur.validation.title_" + event.statut().name().toLowerCase())
                .description("notification.employeur.validation.description_" + event.statut().name().toLowerCase())
                .notificationType(NotificationType.OFFRE_STATUT_CHANGE)
                .dateCreaction(LocalDateTime.now())
                .role(Role.EMPLOYEUR)
                .urlCible("/employeur/offres")
                .destinataire(destinataire)
                .variables(Map.of(
                        VariableMapKey.OFFRE_TITRE, offre.getTitle(),
                        VariableMapKey.STATUT_FICHIER, event.statut().name()
                ))
                .build();

        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
   public void handleCvSupprimerSoNotification(CvSupprimerSoNotificationGestionnaire event){
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

    @EventListener
    @Transactional
    public NotificationDTO handleNouvelleOffreTeleversee(
            NouvelleOffreTeleverseeEvent event) {

        OffreDeStage offre = event.offre();

        Notification notification = Notification.builder()
                .titre("notification.gestionnaire.offre.title")
                .description("notification.gestionnaire.offre.description")
                .role(Role.GESTIONNAIRE)
                .notificationType(NotificationType.OFFRE_ATTENTE_VALIDATION)
                .dateCreaction(LocalDateTime.now())
                .urlCible(ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL
                        .build(String.valueOf(offre.getId())))
                .variables(Map.of(
                        VariableMapKey.OFFRE_ID, String.valueOf(offre.getId()),
                        VariableMapKey.OFFRE_TITRE, offre.getTitle()
                ))
                .build();

        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
    public void handleOffreDeStageApprouvee(OffreDeStageApprouveeEvent event) {
        OffreDeStage offre = event.offre();

        List<Etudiant> etudiants =
                etudiantRepository.findAllByDepartmentAndCv_Statut(
                        offre.getDomain(),
                        Statut.ACCEPTEE
                );

        for (Etudiant etudiant : etudiants) {
            Notification notification = new Notification(
                    "notification.etudiant.nouvelleOffre.titre",
                    "notification.etudiant.nouvelleOffre.description",
                    NotificationType.NOUVELLE_OFFRE_STAGE,
                    LocalDateTime.now(),
                    ControllerPath.ETUDIANT_OFFRES.build(),
                    etudiant,
                    Role.ETUDIANT,
                    Map.of(
                            VariableMapKey.OFFRE_ID,
                            String.valueOf(offre.getId()),
                            VariableMapKey.OFFRE_TITRE,
                            offre.getTitle()
                    )
            );

            notificationRepository.save(notification);
        }
    }

    @EventListener
    @Transactional
    public void handleCvSupprimerSoNotificationEtudiant(CvSupprimerSoNotificationEtudiant event){
        Etudiant etudiant = event.etudiant();
        notificationRepository.deleteNotificationByNotificationTypeAndDestinataire(NotificationType.CV_STATUT_CHANGE,etudiant);
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



    @EventListener
    @Transactional
    public NotificationDTO handleNouvelleCandidatureEvent(NouvelleCandidatureEvent event) {
            Candidature candidature = event.candidature();
            OffreDeStage offre = candidature.getOffreDeStage();
            Etudiant etudiant = candidature.getEtudiant();

            Notification notification = Notification.builder()
                    .titre("notification.employeur.nouvelleCandidature.title")
                    .description("notification.employeur.nouvelleCandidature.description")
                    .notificationType(NotificationType.NOUVELLE_CANDIDATURE)
                    .role(Role.EMPLOYEUR)
                    .destinataire(offre.getEmployeur())
                    .dateCreaction(LocalDateTime.now())
                    .urlCible(ControllerPath.EMPLOYEUR_CANDIDATURES.build())
                    .variables(Map.of(
                            VariableMapKey.ETUDIANT_NOM,
                            etudiant.getFirstName() + " " + etudiant.getLastName(),
                            VariableMapKey.OFFRE_TITRE,
                            offre.getTitle()
                    ))
                    .build();

            return NotificationDTO.of(
                    notificationRepository.save(notification)
            );
    }
}