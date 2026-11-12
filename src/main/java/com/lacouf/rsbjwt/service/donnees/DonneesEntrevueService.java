package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.repository.EntrevueRepository;
import com.lacouf.rsbjwt.service.dto.EntrevueDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesEntrevueService {
    private final EntrevueRepository entrevueRepository;

    public DonneesEntrevueService(EntrevueRepository entrevueRepository) {
        this.entrevueRepository = entrevueRepository;
    }

    public List<EntrevueDTO> obtenirEntrevuesAVenir() {
        return entrevueRepository.findByDateHeureEntrevueAfterAndStatutInOrderByDateHeureEntrevueAsc(
                        LocalDateTime.now(), List.of(StatutEntrevue.A_CONFIRMER, StatutEntrevue.CONFIRMEE))
                .stream().map(EntrevueDTO::of).toList();
    }

    public long compterEntrevuesParStatut(StatutEntrevue statut) {
        if (statut == null) throw new IllegalArgumentException("Le statut d'entrevue est obligatoire.");
        return entrevueRepository.countByStatut(statut);
    }
}