package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Notification.ControllerPath;
import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.*;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    private Etudiant etudiant;
    private CvEtudiant cvEtudiant;
    private Gestionnaire gestionnaire;
    private Employeur employeur;
    private Notification notificationEmployeur;
    private Notification notificationWithoutUser;
    private Notification notificationWithUser;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void init(){
        etudiant = new Etudiant("Jean","Dupuis","Jean.Dupuis@hotmail.com","438-956-5858", Departement.INFORMATIQUE,"8585858","YOYO");
        cvEtudiant = new CvEtudiant("JeanDupuisTutorial","application/pdf",200L,"uploads/cvs/JeanDupuis.pdf", LocalDateTime.now(),etudiant);
        notificationWithoutUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",null, Role.GESTIONNAIRE, Map.of(VariableMapKey.ETUDIANT_NOM,etudiant.getFirstName() + " " + etudiant.getLastName()));
        notificationWithoutUser.setId(1L);
        notificationWithUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",etudiant, Role.ETUDIANT, new HashMap<>());
        notificationWithUser.setId(2L);
        gestionnaire = new Gestionnaire(3L,"Patrick","Mesange","Patrick.Mesange@hotmail.com","NoNeed","450-254-5874");
        gestionnaire.setId(3L);
        employeur = Employeur.builder()
                .firstName("Roger")
                .lastName("Malinois")
                .email("Roger.Malinois@hotmail.com")
                .password("test")
                .businessName("Malinois Inc")
                .build();
        employeur.setId(10L);
        notificationEmployeur = new Notification(
                "notification.employeur.nouvelleCandidature.title",
                "notification.employeur.nouvelleCandidature.description",
                NotificationType.NOUVELLE_CANDIDATURE,
                LocalDateTime.now(), "/employeur", employeur, Role.EMPLOYEUR,
                Map.of(VariableMapKey.ETUDIANT_NOM, "Jean Dupuis",
                        VariableMapKey.OFFRE_TITRE, "Developpeur Java"));
        notificationEmployeur.setId(40L);
    }

    @Test
    void doitCreerNotificationPourEmployeurLorsNouvelleCandidature() {
        OffreDeStage offre = new OffreDeStage();
        offre.setId(20L);
        offre.setTitle("Developpeur Java");
        offre.setEmployeur(employeur);
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(30L);
        notificationEmployeur.setVariables(Map.of(
                VariableMapKey.ETUDIANT_NOM, "Jean Dupuis",
                VariableMapKey.OFFRE_TITRE, "Developpeur Java",
                VariableMapKey.CANDIDATURE_ID, String.valueOf(candidature.getId())
        ));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationEmployeur);

        NotificationDTO notificationDTO = notificationService.handleNouvelleCandidatureEvent(
                new NouvelleCandidatureEvent(candidature));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getDestinataire()).isSameAs(employeur);
        assertThat(notification.getRole()).isEqualTo(Role.EMPLOYEUR);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.NOUVELLE_CANDIDATURE);
        assertThat(notification.getTitre()).isEqualTo("notification.employeur.nouvelleCandidature.title");
        assertThat(notification.getDescription()).isEqualTo("notification.employeur.nouvelleCandidature.description");
        assertThat(notification.getUrlCible()).isEqualTo("/employeur");
        assertThat(notification.getVariables()).isEqualTo(notificationEmployeur.getVariables());
        assertThat(notification.isEstLu()).isFalse();
        assertThat(notification.getDateNotifiee()).isNotNull();
        assertThat(notificationDTO).isEqualTo(NotificationDTO.of(notificationEmployeur));
    }

    @Test
    void doitRecevoirNotificationsParDestinatairePourEmployeur() throws UtilisateurIntrouvableException, AucunRoleDefini {
        when(userAppRepository.findById(employeur.getId())).thenReturn(Optional.of(employeur));
        when(notificationRepository.getNotificationByDestinataire(employeur))
                .thenReturn(List.of(notificationEmployeur));

        List<NotificationDTO> notificationDTOList = notificationService.recevoirNotifications(employeur.getId());

        assertThat(notificationDTOList.size()).isEqualTo(1);
        assertThat(notificationDTOList.getFirst()).isEqualTo(NotificationDTO.of(notificationEmployeur));
        verify(notificationRepository).getNotificationByDestinataire(employeur);
        verify(notificationRepository, never()).getNotificationByRole(any());
    }

    @Test
    void doitCompterNotificationsParDestinatairePourEmployeur() throws UtilisateurIntrouvableException, AucunRoleDefini {
        when(userAppRepository.findById(employeur.getId())).thenReturn(Optional.of(employeur));
        when(notificationRepository.totalByUser(employeur)).thenReturn(2L);

        Long nombreDeNotification = notificationService.compterNotifications(employeur.getId());

        assertThat(nombreDeNotification).isEqualTo(2L);
        verify(notificationRepository).totalByUser(employeur);
        verify(notificationRepository, never()).totalByRole(any());
    }

    @Test
    void doitCreerNotificationLorsDunNouveauCVTeleverser(){
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithoutUser);
        NouveauCvTeleverserEvent nouveauCvTeleverserEvent = new NouveauCvTeleverserEvent(cvEtudiant);

        NotificationDTO notificationDTO = notificationService.handleNouveauCVTeleverser(nouveauCvTeleverserEvent);

        assertThat(notificationDTO).isNotNull();
        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isEqualTo(notificationWithoutUser.getVariables().get(VariableMapKey.ETUDIANT_NOM));
    }

    @Test
    void doitCreerUneNotificationLorsValidationCvApprouver(){
        cvEtudiant.setStatut(Statut.ACCEPTEE);
        notificationWithUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",etudiant, Role.ETUDIANT, Map.of(VariableMapKey.STATUT_FICHIER,cvEtudiant.getStatut().name()));
        notificationWithUser.setId(4L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithUser);
        CvStatutModifieEvent cvStatutModifieEvent = new CvStatutModifieEvent(notificationWithUser.getDestinataire(),cvEtudiant.getStatut());
        NotificationDTO notificationDTO = notificationService.handleCvStatutModifieEvent(cvStatutModifieEvent);

        assertThat(notificationDTO).isNotNull();
        assertThat(notificationDTO.variables().get(VariableMapKey.STATUT_FICHIER)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.STATUT_FICHIER)).isEqualTo(notificationWithUser.getVariables().get(VariableMapKey.STATUT_FICHIER));
        assertThat(notificationDTO.id()).isEqualTo(notificationWithUser.getId());
    }

    @Test
    void doitSupprimerNotificationLorsCvSupprimer() {
        String expectedUrl = ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build("1");


        CvSupprimerSoNotificationGestionnaireEvent event = new CvSupprimerSoNotificationGestionnaireEvent(expectedUrl);

        notificationService.handleCvSupprimerSoNotification(event);

        verify(notificationRepository, times(1)).deleteByUrlCible(expectedUrl);
    }

    @Test
    void doitCreerUneNotificationLorsValidationCvRefuser(){
        cvEtudiant.setStatut(Statut.REFUSEE);
        notificationWithUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",etudiant, Role.ETUDIANT, Map.of(VariableMapKey.STATUT_FICHIER,cvEtudiant.getStatut().name()));
        notificationWithUser.setId(4L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithUser);
        CvStatutModifieEvent cvStatutModifieEvent = new CvStatutModifieEvent(notificationWithUser.getDestinataire(),cvEtudiant.getStatut());
        NotificationDTO notificationDTO = notificationService.handleCvStatutModifieEvent(cvStatutModifieEvent);

        assertThat(notificationDTO).isNotNull();
        assertThat(notificationDTO.variables().get(VariableMapKey.STATUT_FICHIER)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.STATUT_FICHIER)).isEqualTo(notificationWithUser.getVariables().get(VariableMapKey.STATUT_FICHIER));
        assertThat(notificationDTO.id()).isEqualTo(notificationWithUser.getId());
    }

    @Test
    void doitRecevoirTousLesNotificationParRoleGestionnaire() throws UtilisateurIntrouvableException, AucunRoleDefini {
        when(notificationRepository.getNotificationByRole(Role.GESTIONNAIRE)).thenReturn(List.of(notificationWithoutUser));
        when(userAppRepository.findById(gestionnaire.getId())).thenReturn(Optional.of(gestionnaire));

        List<NotificationDTO> notificationDTOList = notificationService.recevoirNotifications(gestionnaire.getId());

        assertThat(notificationDTOList.size()).isEqualTo(1);
        NotificationDTO notificationDTO = notificationDTOList.getFirst();
        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isEqualTo(notificationWithoutUser.getVariables().get(VariableMapKey.ETUDIANT_NOM));
        verify(notificationRepository).getNotificationByRole(Role.GESTIONNAIRE);
        verify(notificationRepository, never()).getNotificationByDestinataire(etudiant);
    }

    @Test
    void doitRecevoirTousLesNotificationDuDestinataire() throws UtilisateurIntrouvableException, AucunRoleDefini {
        when(notificationRepository.getNotificationByDestinataire(any(Etudiant.class))).thenReturn(List.of(notificationWithUser));
        when(userAppRepository.findById(etudiant.getId())).thenReturn(Optional.of(etudiant));

        List<NotificationDTO> notificationDTOList = notificationService.recevoirNotifications(etudiant.getId());

        assertThat(notificationDTOList.size()).isEqualTo(1);
        NotificationDTO notificationDTO = notificationDTOList.getFirst();
        assertThat(notificationDTO.id()).isEqualTo(notificationWithUser.getId());
        assertThat(notificationDTO.variables()).isEmpty();
        verify(notificationRepository).getNotificationByDestinataire(etudiant);
    }

    @Test
    void doitEnvoyerAucunRoleExceptionLorsrecevoirNotifications() {
        UserApp utilisateurSansRole = mock(UserApp.class);
        when(utilisateurSansRole.getRole()).thenReturn(null);
        when(userAppRepository.findById(5L)).thenReturn(Optional.of(utilisateurSansRole));

       assertThatThrownBy(() -> notificationService.recevoirNotifications(5L)).isInstanceOf(AucunRoleDefini.class);
       verify(userAppRepository).findById(5L);
    }

    @Test
    void doitMettreNotificationALue() throws NotificationNonTrouvee {
        when(notificationRepository.findById(notificationWithoutUser.getId())).thenReturn(Optional.of(notificationWithoutUser));
        when(notificationRepository.save(notificationWithoutUser)).thenReturn(notificationWithoutUser);

        NotificationDTO notificationDTO = notificationService.setNotificationLue(notificationWithoutUser.getId());

        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isEqualTo(notificationWithoutUser.getVariables().get(VariableMapKey.ETUDIANT_NOM));        assertThat(notificationDTO.estLu()).isTrue();
        assertThat(notificationDTO.estLu()).isTrue();
        verify(notificationRepository).save(notificationWithoutUser);
    }

    @Test
    void doitEnvoyerNotificationNonTrouveeSetNotificationLue() {
        when(notificationRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.setNotificationLue(5L)).isInstanceOf(NotificationNonTrouvee.class);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void doitSupprimerNotification() throws NotificationNonTrouvee {
        when(notificationRepository.findById(notificationWithoutUser.getId())).thenReturn(Optional.of(notificationWithoutUser));

        NotificationDTO notificationDTO = notificationService.supprimerNotification(notificationWithoutUser.getId());

        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isNotEmpty();
        assertThat(notificationDTO.variables().get(VariableMapKey.ETUDIANT_NOM)).isEqualTo(notificationWithoutUser.getVariables().get(VariableMapKey.ETUDIANT_NOM));        assertThat(notificationDTO.estLu()).isFalse();
        verify(notificationRepository).findById(notificationWithoutUser.getId());
        verify(notificationRepository).deleteById(notificationWithoutUser.getId());
    }

    @Test
    void doitEnvoyerNotificationNonTrouveeSupprimerNotification() {
        assertThatThrownBy(() -> notificationService.supprimerNotification(notificationWithoutUser.getId())).isInstanceOf(NotificationNonTrouvee.class);
    }

    @Test
    void doitRecevoirLeNombreDeNotificationParRoleGestionnaire() throws UtilisateurIntrouvableException, AucunRoleDefini {
        List<Notification> notifications = List.of(notificationWithoutUser);
        when(notificationRepository.totalByRole(Role.GESTIONNAIRE)).thenReturn((long) notifications.size());
        when(userAppRepository.findById(gestionnaire.getId())).thenReturn(Optional.of(gestionnaire));

        Long nombreDeNotification = notificationService.compterNotifications(gestionnaire.getId());

        assertThat(nombreDeNotification).isEqualTo(notifications.size());
        verify(notificationRepository).totalByRole(Role.GESTIONNAIRE);
        verify(notificationRepository, never()).totalByUser(etudiant);
    }

    @Test
    void doitRecevoirLeNombreDeNotificationParDestinataire() throws UtilisateurIntrouvableException, AucunRoleDefini {
        List<Notification> notifications = List.of(notificationWithoutUser);
        when(notificationRepository.totalByUser(etudiant)).thenReturn((long) notifications.size());
        when(userAppRepository.findById(etudiant.getId())).thenReturn(Optional.of(etudiant));

        Long nombreDeNotification = notificationService.compterNotifications(etudiant.getId());

        assertThat(nombreDeNotification).isEqualTo(notifications.size());
        verify(notificationRepository, never()).totalByRole(Role.GESTIONNAIRE);
        verify(notificationRepository).totalByUser(etudiant);
    }

    @Test
    void doitEnvoyerAucunRoleExceptionLorsNombreDeNotification() {
        UserApp utilisateurSansRole = mock(UserApp.class);
        when(utilisateurSansRole.getRole()).thenReturn(null);
        when(userAppRepository.findById(5L)).thenReturn(Optional.of(utilisateurSansRole));

        assertThatThrownBy(() -> notificationService.compterNotifications(5L)).isInstanceOf(AucunRoleDefini.class);
        verify(userAppRepository).findById(5L);
    }

    @Test
    void doitCreerUneNotificationLorsNouvelleOffreTeleversee() {
        OffreDeStage offre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();

        offre.setId(10L);

        Notification notification = new Notification(
                "notification.titre",
                "notification.description",
                NotificationType.OFFRE_ATTENTE_VALIDATION,
                LocalDateTime.now(),
                ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL.build("10"),
                null,
                Role.GESTIONNAIRE,
                Map.of(
                        VariableMapKey.OFFRE_ID, "10",
                        VariableMapKey.OFFRE_TITRE, "Développeur frontend",
                        VariableMapKey.EMPLOYEUR_NOM, employeur.getBusinessName()
                )
        );

        notification.setId(5L);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        NouvelleOffreTeleverseeEvent event =
                new NouvelleOffreTeleverseeEvent(offre);

        NotificationDTO notificationDTO =
                notificationService.handleNouvelleOffreTeleversee(event);

        assertThat(notificationDTO).isNotNull();
        assertThat(notificationDTO.id()).isEqualTo(notification.getId());
        assertThat(notificationDTO.variables().get(VariableMapKey.OFFRE_ID))
                .isEqualTo("10");
        assertThat(notificationDTO.variables().get(VariableMapKey.OFFRE_TITRE))
                .isEqualTo("Développeur frontend");

        verify(notificationRepository).save(any(Notification.class));
    }


    @Test
    void doitCreerUneNotificationLorsApprobationNouvelleOffre() {
        OffreDeStage offre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .employeur(employeur)
                .salary(25.0)
                .domain(Departement.INFORMATIQUE)
                .statut(Statut.ACCEPTEE)
                .createdAt(LocalDateTime.now())
                .build();

        offre.setId(10L);

        Notification notification = new Notification(
                "notification.etudiant.nouvelleOffre.titre",
                "notification.etudiant.nouvelleOffre.description",
                NotificationType.NOUVELLE_OFFRE_STAGE,
                LocalDateTime.now(),
                ControllerPath.ETUDIANT_OFFRES.build(),
                etudiant,
                Role.ETUDIANT,
                Map.of(
                        VariableMapKey.OFFRE_ID, "10",
                        VariableMapKey.OFFRE_TITRE, "Développeur frontend",
                        VariableMapKey.EMPLOYEUR_NOM, employeur.getBusinessName()
                )
        );

        notification.setId(6L);

        when(etudiantRepository.findAllByDepartmentAndCv_Statut(
                Departement.INFORMATIQUE,
                Statut.ACCEPTEE
        )).thenReturn(List.of(etudiant));

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notification);

        OffreDeStageApprouveeEvent event =
                new OffreDeStageApprouveeEvent(offre);

        notificationService.handleOffreDeStageApprouvee(event);

        verify(etudiantRepository).findAllByDepartmentAndCv_Statut(
                Departement.INFORMATIQUE,
                Statut.ACCEPTEE
        );

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void doitCreerUneNotificationLorsChamgementStatutOffre(){
        OffreDeStage offre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationEmployeur);

        NotificationDTO notificationDTO = notificationService.handleOffreStatutModifie(new OffreStatutModifieEvent(gestionnaire,offre.getStatut(),offre));

        assertThat(notificationDTO.description()).isEqualTo(notificationEmployeur.getDescription());
        assertThat(notificationDTO.variables()).isEqualTo(notificationEmployeur.getVariables());
    }

    @Test
    void doitModifierUneNotificationLorsDeLaModificationOffre() throws NotificationNonTrouvee {
        OffreDeStage ancienneOffre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        OffreDeStage nouvelleOffre = OffreDeStage.builder()
                .title("Développeur backend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        String urlCible = ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL
                .build(String.valueOf(ancienneOffre.getId()));
        when(notificationRepository.findByNotificationTypeAndUrlCible(
                NotificationType.OFFRE_ATTENTE_VALIDATION, urlCible))
                .thenReturn(Optional.of(notificationEmployeur));
        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepository.save(notificationCaptor.capture()))
                .thenReturn(notificationEmployeur);

        notificationService.handleOffreModifierSoNotification(
                new OffreModifierSoNotificationEvent(ancienneOffre, nouvelleOffre)
        );
        Notification notificationSauvegardee = notificationCaptor.getValue();

        assertThat(notificationSauvegardee.getDescription())
                .isEqualTo(notificationEmployeur.getDescription());
        assertThat(notificationSauvegardee.getVariables().get(VariableMapKey.OFFRE_TITRE))
                .isEqualTo("Développeur backend");
    }

    @Test
    void doitRenvoyerNotificationNontrouver() throws NotificationNonTrouvee {
        OffreDeStage ancienneOffre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        OffreDeStage nouvelleOffre = OffreDeStage.builder()
                .title("Développeur backend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(Statut.EN_ATTENTE)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        String urlCible = ControllerPath.GESTIONNAIRE_OFFRE_ID_DETAIL
                .build(String.valueOf(ancienneOffre.getId()));

        when(notificationRepository.findByNotificationTypeAndUrlCible(
                NotificationType.OFFRE_ATTENTE_VALIDATION, urlCible))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.handleOffreModifierSoNotification(
                new OffreModifierSoNotificationEvent(ancienneOffre, nouvelleOffre)))
                .isInstanceOf(NotificationNonTrouvee.class);

    }

    @Test
    void doitSupprimerLaNotificationLorsDeLaSuppressionOffre() {
        String navigationPath = "/gestionnaire/offres/12";
        OffreSupprimerSoNotificationGestionnaireEvent event =
                new OffreSupprimerSoNotificationGestionnaireEvent(navigationPath);

        notificationService.handleOffreSupprimerSoNotification(event);

        verify(notificationRepository).deleteByUrlCible(navigationPath);
    }

    @Test
    void doitSupprimerLaNotificationCvEtudiant() {
        CvSupprimerSoNotificationEtudiantEvent event =
                new CvSupprimerSoNotificationEtudiantEvent(etudiant);

        notificationService.handleCvSupprimerSoNotificationEtudiant(event);

        verify(notificationRepository).deleteNotificationByNotificationTypeAndDestinataire(
                NotificationType.CV_STATUT_CHANGE,
                etudiant
        );
    }



}