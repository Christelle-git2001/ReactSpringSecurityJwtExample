package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.Exception.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.model.Entrevue;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.repository.EntrevueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonneesEntrevueServiceTest {
    @Mock EntrevueRepository entrevues;
    @InjectMocks DonneesEntrevueService service;

    @Test
    void rechercheLesEntrevuesFuturesAConfirmerOuConfirmeesDansOrdreChronologique() {
        var entrevue = new Entrevue();
        entrevue.setId(6L);
        entrevue.setStatut(StatutEntrevue.CONFIRMEE);
        entrevue.setDateHeureEntrevue(LocalDateTime.now().plusDays(3));
        var avant = LocalDateTime.now();
        var statuts = List.of(StatutEntrevue.A_CONFIRMER, StatutEntrevue.CONFIRMEE);
        when(entrevues.findByDateHeureEntrevueAfterAndStatutInOrderByDateHeureEntrevueAsc(any(), eq(statuts)))
                .thenReturn(List.of(entrevue));

        var resultat = service.obtenirEntrevuesAVenir();
        var apres = LocalDateTime.now();

        assertThat(resultat).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(6L);
            assertThat(dto.statut()).isEqualTo(StatutEntrevue.CONFIRMEE);
            assertThat(dto.dateHeureEntrevue()).isEqualTo(entrevue.getDateHeureEntrevue());
        });
        verify(entrevues).findByDateHeureEntrevueAfterAndStatutInOrderByDateHeureEntrevueAsc(
                argThat(date -> !date.isBefore(avant) && !date.isAfter(apres)), eq(statuts));
    }

    @ParameterizedTest
    @EnumSource(StatutEntrevue.class)
    void compteLesEntrevuesDuStatutDemande(StatutEntrevue statut) throws ParametreDonneesInvalideException {
        when(entrevues.countByStatut(statut)).thenReturn(3L);

        assertThat(service.compterEntrevuesParStatut(statut)).isEqualTo(3L);
        verify(entrevues).countByStatut(statut);
        verifyNoMoreInteractions(entrevues);
    }

    @Test
    void refuseUnStatutAbsent() {
        assertThatExceptionOfType(ParametreDonneesInvalideException.class)
                .isThrownBy(() -> service.compterEntrevuesParStatut(null))
                .withMessage("chatbot.errors.interview_status_required");
        verifyNoInteractions(entrevues);
    }
}