package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.CommentaireRefusObligatoireException;
import com.lacouf.rsbjwt.Exception.OffreIntrouvableException;
import com.lacouf.rsbjwt.Exception.OffreNonEnAttenteException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.StatutOffre;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.service.dto.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.SecteurEmployeurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GestionnaireServiceTest {

    private OffreDeStageRepository offreDeStageRepository;

    private GestionnaireService gestionnaireService;

    @BeforeEach
    void setUp() {
        offreDeStageRepository = mock(OffreDeStageRepository.class);
        gestionnaireService = new GestionnaireService(offreDeStageRepository);
    }

    private Employeur creerEmployeurTest() {
        Employeur employeur = Employeur.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .phone("5141234567")
                .email("jean.dupont@test.com")
                .town("Montréal")
                .businessName("Entreprise Test")
                .businessSector(SecteurActivite.INFORMATIQUE)
                .password("password")
                .build();

        employeur.setId(1L);

        return employeur;
    }

    @Test
    void doitMapperEtRetournerTousLesSecteurs() {
        List<SecteurEmployeurDTO> result =
                gestionnaireService.getAllSecteurs();

        assertThat(result)
                .hasSize(SecteurActivite.values().length)
                .extracting(SecteurEmployeurDTO::label)
                .contains(
                        SecteurActivite.INFORMATIQUE.getLabel(),
                        SecteurActivite.PHARMACEUTIQUE.getLabel(),
                        SecteurActivite.FINANCE.getLabel()
                );
    }

    @Test
    void doitRetournerLesOffresEnAttente() {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findByStatut(StatutOffre.EN_ATTENTE))
                .thenReturn(List.of(offre));

        List<OffreDeStageDTO> result =
                gestionnaireService.getOffresEnAttente();

        assertThat(result)
                .hasSize(1)
                .extracting(OffreDeStageDTO::id)
                .contains(1L);

        verify(offreDeStageRepository)
                .findByStatut(StatutOffre.EN_ATTENTE);
    }

    @Test
    void doitApprouverUneOffreEnAttente()
            throws OffreIntrouvableException, OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        when(offreDeStageRepository.save(offre))
                .thenReturn(offre);

        OffreDeStageDTO result =
                gestionnaireService.approuverOffre(1L);

        assertThat(result).isNotNull();

        assertThat(offre.getStatut())
                .isEqualTo(StatutOffre.ACCEPTEE);

        assertThat(offre.getRejectionComment())
                .isNull();

        verify(offreDeStageRepository)
                .findById(1L);

        verify(offreDeStageRepository)
                .save(offre);
    }

    @Test
    void doitLeverExceptionSiOffreIntrouvable()
            throws OffreNonEnAttenteException {

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                gestionnaireService.approuverOffre(1L)
        )
                .isInstanceOf(OffreIntrouvableException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiOffreNonEnAttente()
            throws OffreIntrouvableException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.ACCEPTEE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.approuverOffre(1L)
        )
                .isInstanceOf(OffreNonEnAttenteException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitRefuserUneOffreEnAttente()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException,
            CommentaireRefusObligatoireException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);
        offre.setEmployeur(creerEmployeurTest());

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        when(offreDeStageRepository.save(offre))
                .thenReturn(offre);

        String commentaire =
                "L'offre ne respecte pas les exigences.";

        OffreDeStageDTO result =
                gestionnaireService.refuserOffre(1L, commentaire);

        assertThat(result).isNotNull();

        assertThat(offre.getStatut())
                .isEqualTo(StatutOffre.REFUSEE);

        assertThat(offre.getRejectionComment())
                .isEqualTo(commentaire);

        verify(offreDeStageRepository)
                .findById(1L);

        verify(offreDeStageRepository)
                .save(offre);
    }

    @Test
    void doitLeverExceptionSiCommentaireNull()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, null)
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiCommentaireVide()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, "")
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiCommentaireContientSeulementDesEspaces()
            throws OffreIntrouvableException,
            OffreNonEnAttenteException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.EN_ATTENTE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(1L, "   ")
        )
                .isInstanceOf(CommentaireRefusObligatoireException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }

    @Test
    void doitLeverExceptionSiOffreRefuseeNestPasEnAttente()
            throws OffreIntrouvableException,
            CommentaireRefusObligatoireException {

        OffreDeStage offre = new OffreDeStage();
        offre.setId(1L);
        offre.setStatut(StatutOffre.ACCEPTEE);

        when(offreDeStageRepository.findById(1L))
                .thenReturn(Optional.of(offre));

        assertThatThrownBy(() ->
                gestionnaireService.refuserOffre(
                        1L,
                        "L'offre ne respecte pas les exigences."
                )
        )
                .isInstanceOf(OffreNonEnAttenteException.class);

        verify(offreDeStageRepository, never())
                .save(any());
    }
}