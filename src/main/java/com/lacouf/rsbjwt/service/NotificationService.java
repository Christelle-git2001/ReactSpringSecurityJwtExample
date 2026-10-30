package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.*;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        String urlCible = ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build(String.valueOf(cv.getId()));
        Optional<Notification> optionalNotification = notificationRepository
                .findByNotificationTypeAndUrlCible(NotificationType.CV_ATTENTE_VALIDATION, urlCible);
        Notification notification;

        if (optionalNotification.isPresent()) {
            notification = optionalNotification.get();
            notification.setDateNotifiee(LocalDateTime.now());
            notification.setEstLu(false);
            notification.setVariables(Map.of(
                    VariableMapKey.ETUDIANT_NOM, etudiant.getFirstName() + " " + etudiant.getLastName()
            ));
        } else {
            notification = Notification.builder()
                    .titre("notification.gestionnaire.televerserCV.title")
                    .description("notification.gestionnaire.televerserCV.description")
                    .role(Role.GESTIONNAIRE)
                    .notificationType(NotificationType.CV_ATTENTE_VALIDATION)
                    .dateCreaction(LocalDateTime.now())
                    .urlCible(urlCible)
                    .variables(Map.of(
                            VariableMapKey.ETUDIANT_NOM, etudiant.getFirstName() + " " + etudiant.getLastName()
                    ))
                    .build();
        }
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
        Statut statut = event.statut();

        Notification notification = Notification.builder()
                .titre("notification.employeur.validation.title_" + statut.name().toLowerCase())
                .description("notification.employeur.validation.description_" + statut.name().toLowerCase())
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
   public void handleCvSupprimerSoNotification(CvSupprimerSoNotificationGestionnaireEvent event){
        notificationRepository.deleteByUrlCible(event.navigationPath());
    }

    @EventListener
    @Transactional
    public void handleOffreSupprimerSoNotification(OffreSupprimerSoNotificationGestionnaireEvent event) {
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
                        VariableMapKey.OFFRE_TITRE, offre.getTitle(),
                        VariableMapKey.ENTREPRISE_NOM,
                        offre.getEmployeur().getBusinessName()
                ))
                .build();

        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
    public void handleOffreModifierSoNotification(OffreModifierSoNotificationEvent event) throws NotificationNonTrouvee {
        OffreDeStage ancienneOffre = event.ancienneOffre();
        OffreDeStage nouvelleOffre = event.nouvelleOffre();
        String urlCible = ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL
                .build(String.valueOf(ancienneOffre.getId()));
        Notification notification = notificationRepository.findByNotificationTypeAndUrlCible(NotificationType.OFFRE_ATTENTE_VALIDATION,urlCible).orElseThrow(NotificationNonTrouvee::new);
        notification.setVariables(Map.of(
                VariableMapKey.OFFRE_ID, String.valueOf(nouvelleOffre.getId()),
                VariableMapKey.OFFRE_TITRE, nouvelleOffre.getTitle(),
                VariableMapKey.ENTREPRISE_NOM,
                nouvelleOffre.getEmployeur().getBusinessName()
        ));
        notification.setDateNotifiee(LocalDateTime.now());
        notificationRepository.save(notification);
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
                            offre.getTitle(),
                            VariableMapKey.ENTREPRISE_NOM,
                            offre.getEmployeur().getBusinessName()
                    )
            );

            notificationRepository.save(notification);
        }
    }

    @EventListener
    @Transactional
    public void handleCvSupprimerSoNotificationEtudiant(CvSupprimerSoNotificationEtudiantEvent event){
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
                            VariableMapKey.OFFRE_TITRE, offre.getTitle(),
                            VariableMapKey.CANDIDATURE_ID, String.valueOf(candidature.getId())
                    ))
                    .build();

            return NotificationDTO.of(
                    notificationRepository.save(notification)
            );
    }


    @EventListener
    @Transactional
    public NotificationDTO handleEntrevueCreee(EntrevueCreeeEvent event) {
        Entrevue entrevue = event.entrevue();

        Candidature candidature = entrevue.getCandidature();
        Etudiant etudiant = candidature.getEtudiant();
        OffreDeStage offre = candidature.getOffreDeStage();

        Notification notification = Notification.builder()
                .titre("notification.etudiant.convocationEntrevue.title")
                .description("notification.etudiant.convocationEntrevue.description")
                .notificationType(NotificationType.CONVOCATION_ENTREVUE)
                .dateCreaction(LocalDateTime.now())
                .role(Role.ETUDIANT)
                .destinataire(etudiant)
                .urlCible(ControllerPath.ETUDIANT_OFFRES.build())
                .variables(Map.of(
                        VariableMapKey.OFFRE_TITRE, offre.getTitle(),
                        VariableMapKey.ENTREPRISE_NOM,
                        offre.getEmployeur().getBusinessName(),
                        VariableMapKey.ENTREVUE_DATE,
                        entrevue.getDateHeureEntrevue().toString()
                ))
                .build();

        return NotificationDTO.of(notificationRepository.save(notification));
    }

    @EventListener
    @Transactional
    public NotificationDTO handleConvocationRepondue(ConvocationRepondueEvent event) {
        Entrevue entrevue = event.entrevue();
        Candidature candidature = entrevue.getCandidature();
        Etudiant etudiant = candidature.getEtudiant();
        OffreDeStage offre = candidature.getOffreDeStage();
        boolean acceptee = entrevue.getStatut() == StatutEntrevue.CONFIRMEE;
        String cle = "notification.employeur." + (acceptee ? "convocationAcceptee" : "convocationRefusee");

        Notification notification = Notification.builder()
                .titre(cle + ".title")
                .description(cle + ".description")
                .notificationType(acceptee ? NotificationType.CONVOCATION_ACCEPTEE : NotificationType.CONVOCATION_REFUSEE)
                .dateCreaction(LocalDateTime.now())
                .role(Role.EMPLOYEUR)
                .destinataire(offre.getEmployeur())
                .urlCible(ControllerPath.EMPLOYEUR_CANDIDATURES.build())
                .variables(Map.of(
                        VariableMapKey.ETUDIANT_NOM, etudiant.getFirstName() + " " + etudiant.getLastName(),
                        VariableMapKey.OFFRE_TITRE, offre.getTitle(),
                        VariableMapKey.CANDIDATURE_ID, String.valueOf(candidature.getId()),
                        VariableMapKey.ENTREVUE_DATE, entrevue.getDateHeureEntrevue().toString()
                ))
                .build();
        return NotificationDTO.of(notificationRepository.save(notification));
    }

}