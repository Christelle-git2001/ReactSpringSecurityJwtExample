package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Entrevue;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.entrevue.EntrevueDTO;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException;
import com.lacouf.rsbjwt.exception.donnees.ParametreDonneesInvalideException.Code;

import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.repository.EntrevueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesEntrevueService {
    private final EntrevueRepository entrevueRepository;
    private final SessionService sessionService;

    public DonneesEntrevueService(EntrevueRepository entrevueRepository, SessionService sessionService) {
        this.entrevueRepository = entrevueRepository;
        this.sessionService = sessionService;
    }

    public List<EntrevueDTO> obtenirEntrevuesAVenir() {
        return entrevueRepository.findByDateHeureEntrevueAfterAndStatutInOrderByDateHeureEntrevueAsc(
                        LocalDateTime.now(), List.of(StatutEntrevue.A_CONFIRMER, StatutEntrevue.CONFIRMEE))
                .stream().map(EntrevueDTO::of).toList();
    }

    public long compterEntrevuesParStatut(StatutEntrevue statut) throws ParametreDonneesInvalideException {
        if (statut == null) throw new ParametreDonneesInvalideException(Code.STATUT_ENTREVUE_OBLIGATOIRE);
        return entrevueRepository.countByStatut(statut);
    }

    public List<EntrevueDTO> obtenirEntrevuesEnAttenteReponse() {
        List<Entrevue> entrevues =
                entrevueRepository.findByStatut(StatutEntrevue.EN_ATTENTE_REPONSE);

        return sessionService.filtrerParSessionActuelle(
                        entrevues,
                        entrevue -> entrevue.getCandidature()
                                .getOffreDeStage()
                                .getCreatedAt()
                )
                .stream()
                .map(EntrevueDTO::of)
                .toList();
    }
}