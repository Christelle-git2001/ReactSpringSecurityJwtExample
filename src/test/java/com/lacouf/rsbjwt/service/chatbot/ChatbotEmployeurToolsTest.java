package com.lacouf.rsbjwt.service.chatbot;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.chatbot.tools.ChatbotEmployeurTools;
import com.lacouf.rsbjwt.service.donnees.DonneesTemporellesService;
import com.lacouf.rsbjwt.service.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatbotEmployeurToolsTest {

    @Mock
    private EmployeurService employeurService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DonneesTemporellesService donneesTemporellesService;

    @InjectMocks
    private ChatbotEmployeurTools chatbotEmployeurTools;

    @Test
    void doitObtenirOffres() throws Exception {
        List<OffreDeStageDTO> mockList = List.of(OffreDeStageDTO.empty());
        when(employeurService.obtenirOffres()).thenReturn(mockList);

        List<OffreDeStageDTO> result = chatbotEmployeurTools.obtenirOffres();

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirOffres();
    }

    @Test
    void doitObtenirCandidatures() throws Exception {
        List<CandidatureDTO> mockList = List.of();
        when(employeurService.obtenirCandidatures()).thenReturn(mockList);

        List<CandidatureDTO> result = chatbotEmployeurTools.obtenirCandidatures();

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirCandidatures();
    }

    @Test
    void doitObtenirCandidaturesConvoquees() throws Exception {
        List<CandidatureDTO> mockList = List.of();
        when(employeurService.obtenirCandidaturesConvoquees()).thenReturn(mockList);

        List<CandidatureDTO> result = chatbotEmployeurTools.obtenirCandidaturesConvoquees();

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirCandidaturesConvoquees();
    }

    @Test
    void doitObtenirEntrevues() throws Exception {
        List<EntrevueDTO> mockList = List.of();
        when(employeurService.obtenirEntrevues()).thenReturn(mockList);

        List<EntrevueDTO> result = chatbotEmployeurTools.obtenirEntrevues();

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirEntrevues();
    }

    @Test
    void doitConsulterMesNotificationsNonLues() throws Exception {
        List<NotificationDTO> mockList = List.of();
        when(notificationService.getNotificationsNonLuesEmployeur()).thenReturn(mockList);

        List<NotificationDTO> result = chatbotEmployeurTools.consulterMesNotificationsNonLues();

        assertThat(result).isEqualTo(mockList);
        verify(notificationService).getNotificationsNonLuesEmployeur();
    }

    @Test
    void doitObtenirCandidaturesParOffre() throws Exception {
        Long offreId = 1L;
        List<CandidatureDTO> mockList = List.of();
        when(employeurService.getCandidaturesParOffre(offreId)).thenReturn(mockList);

        List<CandidatureDTO> result = chatbotEmployeurTools.obtenirCandidaturesParOffre(offreId);

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).getCandidaturesParOffre(offreId);
    }

    @Test
    void doitObtenirMesContrats() throws Exception {
        List<ResumeContratDTO> mockList = List.of();
        when(employeurService.obtenirContratsEmployeur()).thenReturn(mockList);

        List<ResumeContratDTO> result = chatbotEmployeurTools.obtenirMesContrats();

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirContratsEmployeur();
    }

    @Test
    void doitObtenirEntrevuesParStatut() throws Exception {
        StatutEntrevue statut = StatutEntrevue.CONFIRMEE;
        List<EntrevueDTO> mockList = List.of();
        when(employeurService.obtenirEntrevuesParStatut(statut)).thenReturn(mockList);

        List<EntrevueDTO> result = chatbotEmployeurTools.obtenirEntrevuesParStatut(statut);

        assertThat(result).isEqualTo(mockList);
        verify(employeurService).obtenirEntrevuesParStatut(statut);
    }

    @Test
    void doitObtenirDateReference() {
        ReferenceTemporelleDTO mockRef = mock(ReferenceTemporelleDTO.class);
        when(donneesTemporellesService.obtenirDateReference()).thenReturn(mockRef);

        ReferenceTemporelleDTO result = chatbotEmployeurTools.obtenirDateReference();

        assertThat(result).isEqualTo(mockRef);
        verify(donneesTemporellesService).obtenirDateReference();
    }
}
