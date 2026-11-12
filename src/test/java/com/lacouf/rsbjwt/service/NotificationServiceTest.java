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
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.*;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.CandidatureRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.CsvSource;
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

    @Mock
    private CandidatureRepository candidatureRepository;

    private Etudiant etudiant;
    private CvEtudiant cvEtudiant;
    private CvEtudiant cvEtudiant2;
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
        cvEtudiant2 = new CvEtudiant("JeanDupuisNonTutorial","application/pdf",200L,"uploads/cvs/JeanDupuis2.pdf", LocalDateTime.now(),etudiant);
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

    private Notification notificationCandidature(NotificationType type, String candidatureId) {
        Notification notification = new Notification();
        notification.setNotificationType(type);
        notification.setVariables(Map.of(VariableMapKey.CANDIDATURE_ID, candidatureId));
        return notification;
    }
    @Test
    void doitNotifierLEmployeurDeLAcceptationDeLaConvocation() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage informatique").employeur(employeur).build();
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        Entrevue entrevue = Entrevue.builder().candidature(candidature)
                .dateHeureEntrevue(LocalDateTime.now().plusDays(1)).build();
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        entrevue.setId(1L);
        Notification convocation = new Notification();
        convocation.setNotificationType(NotificationType.CONVOCATION_ENTREVUE);
        convocation.setVariables(Map.of(VariableMapKey.ENTREVUE_ID, "1"));
        Notification autreConvocation = new Notification();
        autreConvocation.setNotificationType(NotificationType.CONVOCATION_ENTREVUE);
        autreConvocation.setVariables(Map.of(VariableMapKey.ENTREVUE_ID, "2"));
        when(notificationRepository.getNotificationByDestinataire(etudiant))
                .thenReturn(List.of(convocation, autreConvocation));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(50L);
            return notification;
        });

        NotificationDTO resultat = notificationService.handleConvocationRepondue(new ConvocationRepondueEvent(entrevue));

        assertThat(resultat.notificationType()).isEqualTo(NotificationType.CONVOCATION_ACCEPTEE);
        verify(notificationRepository).delete(convocation);
        verify(notificationRepository, never()).delete(autreConvocation);
        String cle = "notification.employeur.convocationAcceptee";
        assertThat(resultat.titre()).isEqualTo(cle + ".title");
        assertThat(resultat.description()).isEqualTo(cle + ".description");
        assertThat(resultat.estLu()).isFalse();
        assertThat(resultat.variables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20")
                .containsEntry(VariableMapKey.OFFRE_TITRE, "Stage informatique")
                .containsEntry(VariableMapKey.ETUDIANT_NOM, etudiant.getFirstName() + " " + etudiant.getLastName())
                .containsEntry(VariableMapKey.ENTREVUE_DATE, entrevue.getDateHeureEntrevue().toString());
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getDestinataire()).isSameAs(employeur);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.EMPLOYEUR);
        assertThat(resultat.urlCible()).isEqualTo(ControllerPath.EMPLOYEUR_CANDIDATURES.build());
    }

    @Test
    void doitNotifierLEmployeurDuRefusDeLaConvocation() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage informatique").employeur(employeur).build();
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        Entrevue entrevue = Entrevue.builder().candidature(candidature)
                .dateHeureEntrevue(LocalDateTime.now().plusDays(1)).build();
        entrevue.setStatut(StatutEntrevue.REFUSEE);
        entrevue.setId(1L);
        Notification convocation = new Notification();
        convocation.setNotificationType(NotificationType.CONVOCATION_ENTREVUE);
        convocation.setVariables(Map.of(VariableMapKey.ENTREVUE_ID, "1"));
        when(notificationRepository.getNotificationByDestinataire(etudiant)).thenReturn(List.of(convocation));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(50L);
            return notification;
        });

        NotificationDTO resultat = notificationService.handleConvocationRepondue(new ConvocationRepondueEvent(entrevue));

        assertThat(resultat.notificationType()).isEqualTo(NotificationType.CONVOCATION_REFUSEE);
        verify(notificationRepository).delete(convocation);
        String cle = "notification.employeur.convocationRefusee";
        assertThat(resultat.titre()).isEqualTo(cle + ".title");
        assertThat(resultat.description()).isEqualTo(cle + ".description");
        assertThat(resultat.estLu()).isFalse();
        assertThat(resultat.variables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20")
                .containsEntry(VariableMapKey.OFFRE_TITRE, "Stage informatique")
                .containsEntry(VariableMapKey.ETUDIANT_NOM, etudiant.getFirstName() + " " + etudiant.getLastName())
                .containsEntry(VariableMapKey.ENTREVUE_DATE, entrevue.getDateHeureEntrevue().toString());
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getDestinataire()).isSameAs(employeur);
        assertThat(captor.getValue().getRole()).isEqualTo(Role.EMPLOYEUR);
        assertThat(resultat.urlCible()).isEqualTo(ControllerPath.EMPLOYEUR_CANDIDATURES.build());
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
    void doitModifierNotificationLorsDuneNotificationDejaPresentePourCV() {
        NouveauCvTeleverserEvent premierEvent = new NouveauCvTeleverserEvent(cvEtudiant);
        NouveauCvTeleverserEvent deuxiemeEvent = new NouveauCvTeleverserEvent(cvEtudiant2);

        String urlCible = ControllerPath.GESTIONNAIRE_CV_ID_DETAIL.build(String.valueOf(cvEtudiant.getId()));

        when(notificationRepository.findByNotificationTypeAndUrlCible(NotificationType.CV_ATTENTE_VALIDATION, urlCible))
                .thenReturn(Optional.empty());
        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notificationWithoutUser);

        NotificationDTO premierDTO = notificationService.handleNouveauCVTeleverser(premierEvent);

        when(notificationRepository.findByNotificationTypeAndUrlCible(NotificationType.CV_ATTENTE_VALIDATION, urlCible))
                .thenReturn(Optional.of(notificationWithoutUser));

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepository.save(notificationCaptor.capture()))
                .thenReturn(notificationWithoutUser);

        NotificationDTO deuxiemeDTO = notificationService.handleNouveauCVTeleverser(deuxiemeEvent);

        Notification notificationSauvegardee = notificationCaptor.getValue();

        assertThat(premierDTO).isNotNull();
        assertThat(deuxiemeDTO).isNotNull();
        assertThat(deuxiemeDTO.id()).isEqualTo(premierDTO.id());

        String nomEtudiantAttendu = cvEtudiant2.getEtudiant().getFirstName() + " " + cvEtudiant2.getEtudiant().getLastName();
        assertThat(notificationSauvegardee.getVariables().get(VariableMapKey.ETUDIANT_NOM))
                .isEqualTo(nomEtudiantAttendu);
        verify(notificationRepository, times(2)).save(any(Notification.class));
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

    @ParameterizedTest
    @EnumSource(value = Statut.class, names = {"ACCEPTEE", "REFUSEE"})
    void doitNotifierLEmployeurDuStatutDeSonOffre(Statut statut){
        OffreDeStage offre = OffreDeStage.builder()
                .title("Développeur frontend")
                .description("Développement d'une application web")
                .salary(25.0)
                .statut(statut)
                .createdAt(LocalDateTime.now())
                .employeur(employeur)
                .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationEmployeur);

        notificationService.handleOffreStatutModifie(new OffreStatutModifieEvent(employeur, statut, offre));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getDestinataire()).isSameAs(employeur);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.OFFRE_STATUT_CHANGE);
        assertThat(notification.getUrlCible()).isEqualTo("/employeur");
        assertThat(notification.getVariables()).containsEntry(VariableMapKey.STATUT_FICHIER, statut.name());
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

    @Test
    void doitMettreAJourNotificationExistanteLorsProfesseurAssigne() {
        Professeur professeur = new Professeur(
                "Marie",
                "Tremblay",
                "marie.tremblay@example.com",
                "password",
                "P123",
                "514-555-0100",
                Departement.INFORMATIQUE
        );
        professeur.setId(99L);
        Notification notificationExistante = Notification.builder()
                .titre("notification.teacher.assignation.title")
                .description("notification.teacher.assignation.description")
                .notificationType(NotificationType.PROFESSEUR_ASSIGNE_ETUDIANT)
                .dateCreaction(LocalDateTime.now())
                .destinataire(professeur)
                .role(Role.PROFESSEUR)
                .urlCible("")
                .variables(new HashMap<>(Map.of(VariableMapKey.ETUDIANT_IDS, "1")))
                .build();

        Etudiant nouvelEtudiant = new Etudiant("Marie", "Tremblay", "marie.tremblay@hotmail.com", "438-956-5859", Departement.INFORMATIQUE, "8585859", "YAYA");
        nouvelEtudiant.setId(2L);

        ProfesseurAssigneEvent event = new ProfesseurAssigneEvent(professeur, List.of(nouvelEtudiant));

        when(notificationRepository.findByDestinataireAndNotificationTypeAndEstLuFalse(
                professeur, NotificationType.PROFESSEUR_ASSIGNE_ETUDIANT))
                .thenReturn(Optional.of(notificationExistante));

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(notificationExistante);

        notificationService.handleProfesseurAssigne(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());

        Notification notificationSauvegardee = captor.getAllValues().get(0);

        assertThat(notificationSauvegardee.getVariables().get(VariableMapKey.ETUDIANT_IDS))
                .isEqualTo("");
    }

    @Test
    void doitCreerUneNotificationPourLeProfesseurEtChaqueEtudiantLorsAssignation() {
        Professeur professeur = new Professeur(
                "Marie",
                "Tremblay",
                "marie.tremblay@example.com",
                "password",
                "P123",
                "514-555-0100",
                Departement.INFORMATIQUE
        );

        Etudiant etudiant2 = new Etudiant(
                "Paul",
                "Martin",
                "paul.martin@example.com",
                "514-555-0101",
                Departement.INFORMATIQUE,
                "E123",
                "password"
        );

        List<Etudiant> etudiants = List.of(etudiant, etudiant2);

        ProfesseurAssigneEvent event =
                new ProfesseurAssigneEvent(professeur, etudiants);

        notificationService.handleProfesseurAssigne(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepository, times(3)).save(captor.capture());

        List<Notification> notifications = captor.getAllValues();

        // Une seule notification pour le professeur
        assertThat(notifications)
                .filteredOn(notification -> notification.getDestinataire() == professeur)
                .hasSize(1);

        // Une notification pour chaque étudiant
        assertThat(notifications)
                .filteredOn(notification -> notification.getRole() == Role.ETUDIANT)
                .hasSize(2);

        assertThat(notifications)
                .filteredOn(notification ->
                        notification.getNotificationType()
                                == NotificationType.PROFESSEUR_ASSIGNE_ETUDIANT)
                .hasSize(1);

        assertThat(notifications)
                .filteredOn(notification ->
                        notification.getNotificationType()
                                == NotificationType.ETUDIANT_ASSIGNE_PROFESSEUR)
                .hasSize(2);
    }

    @Test
    void doitNotifierLEtudiantDeLAcceptationDeSaCandidature() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage Java").employeur(employeur).build();
        offre.setId(10L);
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        Notification reponse = notificationCandidature(NotificationType.CONVOCATION_ACCEPTEE, "20");
        when(notificationRepository.getNotificationByDestinataire(employeur)).thenReturn(List.of(reponse));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(60L);
            return notification;
        });

        notificationService.handleCandidatureAcceptee(new CandidatureAccepteeEvent(candidature));
        verify(notificationRepository).delete(reponse);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getDestinataire()).isSameAs(etudiant);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.CANDIDATURE_ACCEPTEE);
        assertThat(notification.getVariables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20");
    }

    @Test
    void doitNotifierLEtudiantDuRefusDeSaCandidature() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage Java").employeur(employeur).build();
        offre.setId(10L);
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        Notification reponse = notificationCandidature(NotificationType.CONVOCATION_REFUSEE, "20");
        when(notificationRepository.getNotificationByDestinataire(employeur)).thenReturn(List.of(reponse));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(60L);
            return notification;
        });

        notificationService.handleCandidatureRefusee(new CandidatureRefuseeEvent(candidature));
        verify(notificationRepository).delete(reponse);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getDestinataire()).isSameAs(etudiant);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.CANDIDATURE_REFUSEE);
        assertThat(notification.getVariables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20");
        assertThat(notification.getTitre()).isEqualTo("notification.etudiant.candidatureRefusee.title");
    }

    @Test
    void doitNotifierLesDeuxSignatairesQuandLeContratEstCree() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage Java").employeur(employeur).build();
        offre.setId(10L);
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        Contrat contrat = Contrat.builder().candidature(candidature).build();
        contrat.setId(30L);

        notificationService.handleNouveauContratCree(new NouveauContratCreeEvent(contrat));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(Notification::getDestinataire).containsExactly(employeur, etudiant);
        assertThat(captor.getAllValues()).extracting(Notification::getNotificationType)
                .containsOnly(NotificationType.NOUVEAU_CONTRAT);
        verify(notificationRepository).deleteByUrlCible("/gestionnaire/candidatures/20/contrat");
    }

    @Test
    void doitNotifierLeGestionnaireQuandLesDeuxAutresSignaturesSontPresentes() {
        notificationService.handleContratSigne(new ContratSigneEvent(contratAvecDeuxSignatures()));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getRole()).isEqualTo(Role.GESTIONNAIRE);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.CONTRAT_A_SIGNER);
        assertThat(notification.getUrlCible()).isEqualTo("/gestionnaire/candidatures/20/contrat");
        assertThat(notification.getVariables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20");
    }

    @ParameterizedTest
    @CsvSource({"false,false", "true,false", "false,true"})
    void doitAttendreLesDeuxSignaturesAvantDeNotifierLeGestionnaire(boolean etudiantSigne, boolean employeurSigne) {
        Contrat contrat = contratAvecDeuxSignatures();
        if (!etudiantSigne) contrat.setDateSignatureEtudiant(null);
        if (!employeurSigne) contrat.setDateSignatureEmployeur(null);

        notificationService.handleContratSigne(new ContratSigneEvent(contrat));

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void neDoitPasDupliquerLeRappelDeSignatureDuGestionnaire() {
        when(notificationRepository.findByNotificationTypeAndUrlCible(
                NotificationType.CONTRAT_A_SIGNER, "/gestionnaire/candidatures/20/contrat"))
                .thenReturn(Optional.of(new Notification()));

        notificationService.handleContratSigne(new ContratSigneEvent(contratAvecDeuxSignatures()));

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void doitRetirerLeRappelApresLaSignatureDuGestionnaire() {
        Contrat contrat = contratAvecDeuxSignatures();
        contrat.setDateSignatureGestionnaire(LocalDateTime.now());

        notificationService.handleContratSigne(new ContratSigneEvent(contrat));

        verify(notificationRepository).deleteByUrlCible("/gestionnaire/candidatures/20/contrat");
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    private Contrat contratAvecDeuxSignatures() {
        Candidature candidature = new Candidature(etudiant,
                OffreDeStage.builder().title("Stage Java").employeur(employeur).build());
        candidature.setId(20L);
        Contrat contrat = Contrat.builder().candidature(candidature).build();
        contrat.setDateSignatureEtudiant(LocalDateTime.now());
        contrat.setDateSignatureEmployeur(LocalDateTime.now());
        return contrat;
    }

    @Test
    void doitInviterLeGestionnaireADemarrerLeContratQuandUnStageEstTrouve() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage Java").employeur(employeur).build();
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithoutUser);

        notificationService.handleStageTrouveGestionnaire(new StageTrouveEvent(candidature));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getRole()).isEqualTo(Role.GESTIONNAIRE);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.CONTRAT_A_CREER);
        assertThat(notification.getUrlCible()).isEqualTo("/gestionnaire/candidatures/20/contrat");
        assertThat(notification.getVariables())
                .containsEntry(VariableMapKey.CANDIDATURE_ID, "20");
    }

    @Test
    void doitNotifierLEmployeurDuStageChoisiParLEtudiant() {
        OffreDeStage offre = OffreDeStage.builder().title("Stage Java").employeur(employeur).build();
        Candidature candidature = new Candidature(etudiant, offre);
        candidature.setId(20L);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(61L);
            return notification;
        });

        notificationService.handleStageTrouve(new StageTrouveEvent(candidature));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertThat(notification.getDestinataire()).isSameAs(employeur);
        assertThat(notification.getNotificationType()).isEqualTo(NotificationType.STAGE_TROUVE);
        assertThat(notification.getVariables()).containsEntry(VariableMapKey.CANDIDATURE_ID, "20");
    }

    @Test
    void doitNotifierLEtudiantQuandUneEntrevueEstCreee() {
        Candidature candidature = contratAvecDeuxSignatures().getCandidature();
        Entrevue entrevue = Entrevue.builder().candidature(candidature)
                .dateHeureEntrevue(LocalDateTime.of(2027, 1, 15, 10, 0)).build();
        entrevue.setId(30L);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithUser);

        notificationService.handleEntrevueCreee(new EntrevueCreeeEvent(entrevue));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getDestinataire()).isSameAs(etudiant);
        assertThat(captor.getValue().getNotificationType()).isEqualTo(NotificationType.CONVOCATION_ENTREVUE);
        assertThat(captor.getValue().getVariables()).containsEntry(VariableMapKey.ENTREVUE_ID, "30")
                .containsEntry(VariableMapKey.ENTREVUE_DATE, "2027-01-15T10:00");
    }

    @ParameterizedTest
    @EnumSource(value = NotificationType.class, names = {"CONVOCATION_ACCEPTEE", "CONVOCATION_REFUSEE"})
    void doitRetirerSeulementLaReponseDeLEntrevueTerminee(NotificationType type) {
        Entrevue entrevue = Entrevue.builder()
                .candidature(contratAvecDeuxSignatures().getCandidature()).build();
        entrevue.setId(30L);
        Notification reponse = notificationCandidature(type, "20");
        reponse.setVariables(Map.of(VariableMapKey.ENTREVUE_ID, "30"));
        Notification autreEntrevue = notificationCandidature(type, "21");
        autreEntrevue.setVariables(Map.of(VariableMapKey.ENTREVUE_ID, "31"));
        Notification autreType = notificationCandidature(NotificationType.NOUVEAU_CONTRAT, "20");
        when(notificationRepository.getNotificationByDestinataire(employeur))
                .thenReturn(List.of(reponse, autreEntrevue, autreType));

        notificationService.handleEntrevueTerminee(new EntrevueTermineeEvent(entrevue));

        verify(notificationRepository).delete(reponse);
        verify(notificationRepository, never()).delete(autreEntrevue);
        verify(notificationRepository, never()).delete(autreType);
    }

    @Test
    void doitRetirerSeulementLaNouvelleCandidatureRetenuePourEntrevue() {
        Candidature candidature = contratAvecDeuxSignatures().getCandidature();
        Notification nouvelle = notificationCandidature(NotificationType.NOUVELLE_CANDIDATURE, "20");
        Notification autreCandidature = notificationCandidature(NotificationType.NOUVELLE_CANDIDATURE, "21");
        Notification contrat = notificationCandidature(NotificationType.NOUVEAU_CONTRAT, "20");
        when(notificationRepository.getNotificationByDestinataire(employeur))
                .thenReturn(List.of(nouvelle, autreCandidature, contrat));

        notificationService.handleCandidatureRetenue(new CandidatureRetenueEvent(candidature));

        verify(notificationRepository).delete(nouvelle);
        verify(notificationRepository, never()).delete(autreCandidature);
        verify(notificationRepository, never()).delete(contrat);
    }

    @Test
    void doitRetirerLesNotificationsDesCandidaturesFigeesApresLeStageTrouve() {
        Candidature stage = contratAvecDeuxSignatures().getCandidature();
        Candidature autreCandidature = new Candidature(etudiant, stage.getOffreDeStage());
        autreCandidature.setId(21L);
        Notification convocation = notificationCandidature(NotificationType.CONVOCATION_ENTREVUE, "21");
        Notification acceptation = notificationCandidature(NotificationType.CANDIDATURE_ACCEPTEE, "20");
        Notification refus = notificationCandidature(NotificationType.CANDIDATURE_REFUSEE, "21");
        Notification contrat = notificationCandidature(NotificationType.NOUVEAU_CONTRAT, "20");
        Notification nouvelle = notificationCandidature(NotificationType.NOUVELLE_CANDIDATURE, "20");
        Notification confirmee = notificationCandidature(NotificationType.CONVOCATION_ACCEPTEE, "21");
        Notification refusee = notificationCandidature(NotificationType.CONVOCATION_REFUSEE, "21");
        Notification autreEtudiant = notificationCandidature(NotificationType.NOUVELLE_CANDIDATURE, "99");
        when(notificationRepository.getNotificationByDestinataire(etudiant))
                .thenReturn(List.of(convocation, acceptation, refus, contrat));
        when(candidatureRepository.findByEtudiant(etudiant)).thenReturn(List.of(stage, autreCandidature));
        when(notificationRepository.getNotificationByDestinataire(employeur))
                .thenReturn(List.of(nouvelle, confirmee, refusee, autreEtudiant));

        notificationService.handleNotificationsApresStageTrouve(new StageTrouveEvent(stage));

        for (Notification obsolete : List.of(convocation, acceptation, refus, nouvelle, confirmee, refusee)) {
            verify(notificationRepository).delete(obsolete);
        }
        verify(notificationRepository, never()).delete(contrat);
        verify(notificationRepository, never()).delete(autreEtudiant);
    }
}