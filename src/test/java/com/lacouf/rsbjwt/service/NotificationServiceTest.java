package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.event.NouveauCvTeleverser;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    private Etudiant etudiant;
    private CvEtudiant cvEtudiant;
    private Gestionnaire gestionnaire;
    private NouveauCvTeleverser nouveauCvTeleverser;
    private Notification notificationWithoutUser;
    private Notification notificationWithUser;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void init(){
        etudiant = new Etudiant("Jean","Dupuis","Jean.Dupuis@hotmail.com","438-956-5858", Departement.INFORMATIQUE,"8585858","YOYO");
        cvEtudiant = new CvEtudiant("JeanDupuisTutorial","application/pdf",200L,"uploads/cvs/JeanDupuis.pdf", LocalDateTime.now(),etudiant);
        nouveauCvTeleverser = new NouveauCvTeleverser(cvEtudiant);
        notificationWithoutUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",null, Role.GESTIONNAIRE, Map.of(VariableMapKey.ETUDIANT_NOM,etudiant.getFirstName() + " " + etudiant.getLastName()));
        notificationWithoutUser.setId(1L);
        notificationWithUser = new Notification("notification.titre","notification.description", NotificationType.CV_ATTENTE_VALIDATION,LocalDateTime.now(),"/cv/1/download",etudiant, Role.ETUDIANT, new HashMap<>());
        notificationWithUser.setId(2L);
        gestionnaire = new Gestionnaire(3L,"Patrick","Mesange","Patrick.Mesange@hotmail.com","NoNeed","450-254-5874");
        gestionnaire.setId(3L);
    }

    @Test
    void doitCreerNotification(){
        when(notificationRepository.save(any(Notification.class))).thenReturn(notificationWithoutUser);

        NotificationDTO notificationDTO = notificationService.handleNouveauCVTeleverser(nouveauCvTeleverser);

        assertThat(notificationDTO).isNotNull();
        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get("notificationOf.name")).isEqualTo(notificationWithoutUser.getVariables().get("notificationOf.name"));
    }

    @Test
    void doitRecevoirTousLesNotificationParRoleGestionnaire() throws UtilisateurIntrouvableException, AucunRoleDefini {
        when(notificationRepository.getNotificationByRole(Role.GESTIONNAIRE)).thenReturn(List.of(notificationWithoutUser));
        when(userAppRepository.findById(gestionnaire.getId())).thenReturn(Optional.of(gestionnaire));

        List<NotificationDTO> notificationDTOList = notificationService.recevoirNotifications(gestionnaire.getId());

        assertThat(notificationDTOList.size()).isEqualTo(1);
        NotificationDTO notificationDTO = notificationDTOList.getFirst();
        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get("notificationOf.name")).isEqualTo(notificationWithoutUser.getVariables().get("notificationOf.name"));
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
        assertThat(notificationDTO.variables().get("notificationOf.name")).isEqualTo(notificationWithUser.getVariables().get("notificationOf.name"));
        verify(notificationRepository, never()).getNotificationByRole(Role.GESTIONNAIRE);
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
        notificationWithoutUser.setEstLu(true);
        when(notificationRepository.setEstLue(notificationWithoutUser.getId())).thenReturn(Optional.of(notificationWithoutUser));

        NotificationDTO notificationDTO = notificationService.setNotificationLue(notificationWithoutUser.getId());

        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get("notificationOf.name")).isEqualTo(notificationWithoutUser.getVariables().get("notificationOf.name"));
        assertThat(notificationDTO.estLu()).isTrue();
        verify(notificationRepository).setEstLue(notificationWithoutUser.getId());
    }

    @Test
    void doitEnvoyerNotificationNonTrouveeSetNotificationLue() {
        assertThatThrownBy(() -> notificationService.setNotificationLue(5L)).isInstanceOf(NotificationNonTrouvee.class);
    }

    @Test
    void doitSupprimerNotification() throws NotificationNonTrouvee {
        when(notificationRepository.findById(notificationWithoutUser.getId())).thenReturn(Optional.of(notificationWithoutUser));

        NotificationDTO notificationDTO = notificationService.supprimerNotification(notificationWithoutUser.getId());

        assertThat(notificationDTO.id()).isEqualTo(notificationWithoutUser.getId());
        assertThat(notificationDTO.variables().get("notificationOf.name")).isEqualTo(notificationWithoutUser.getVariables().get("notificationOf.name"));
        assertThat(notificationDTO.estLu()).isFalse();
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
}
