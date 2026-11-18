package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesProfesseurService {
    private final ProfesseurRepository professeurRepository;

    public DonneesProfesseurService(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }

    public List<ProfesseurDTO> obtenirProfesseurs() {
        return professeurRepository.findAll().stream().map(ProfesseurDTO::of).toList();
    }

    public List<ProfesseurDTO> obtenirProfesseursSansEtudiants() {
        return professeurRepository.findByEtudiantsIsEmpty().stream().map(ProfesseurDTO::of).toList();
    }
}