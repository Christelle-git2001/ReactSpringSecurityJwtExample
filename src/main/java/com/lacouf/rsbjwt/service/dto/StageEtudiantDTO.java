
package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.service.dto.candidature.ResumeStageEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.EtudiantDTO;

public record StageEtudiantDTO(
        EtudiantDTO etudiant,
        ResumeStageEtudiantDTO stage
) {
}
