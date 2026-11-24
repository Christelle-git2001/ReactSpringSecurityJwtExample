package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.service.dto.utilisateur.ProfesseurDTO;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
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

    public List<ProfesseurDTO> obtenirProfesseurAvecEtudiant() {
        return professeurRepository.findByEtudiantsIsNotEmpty().stream().map(ProfesseurDTO::of).toList();
    }

    public Long compterProfesseurSansEtudiant() {
        return professeurRepository.countProfesseurByEtudiantsIsEmpty();
    }

    public Long compterProfesseurAvecEtudiant() {
        return professeurRepository.countProfesseurByEtudiantsIsNotEmpty();
    }

    public Long compterMoyenneEtudiantParProfesseur() {
        List<Professeur> listeProfesseurs = professeurRepository.findAll();
        if (listeProfesseurs.isEmpty()) {
            return 0L;
        }

        long nombreEleveTotal = listeProfesseurs.stream()
                .mapToLong(p -> p.getEtudiants() != null ? p.getEtudiants().size() : 0L)
                .sum();

        return nombreEleveTotal / listeProfesseurs.size();
    }
}