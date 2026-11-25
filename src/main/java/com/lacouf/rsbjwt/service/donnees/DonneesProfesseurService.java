package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.ProfesseurDTO;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesProfesseurService {
    private final ProfesseurRepository professeurRepository;
    private final SessionService sessionService;

    public DonneesProfesseurService(ProfesseurRepository professeurRepository, SessionService sessionService) {
        this.professeurRepository = professeurRepository;
        this.sessionService = sessionService;
    }

    public List<ProfesseurDTO> obtenirProfesseurs() {
        return professeurRepository.findAll().stream().map(ProfesseurDTO::of).toList();
    }

    public List<ProfesseurDTO> obtenirProfesseursSansEtudiants() {
        return professeurRepository.findByEtudiantsIsEmpty().stream().map(ProfesseurDTO::of).toList();
    }

    public Long compterProfesseurSansEtudiant() {
        return professeurRepository.countProfesseurByEtudiantsIsEmpty();
    }

    public Long compterProfesseurAvecEtudiant() {
        return professeurRepository.countProfesseurByEtudiantsIsNotEmpty();
    }

    public Long compterMoyenneEtudiantParProfesseur() {
        List<Professeur> professeurs = professeurRepository.findAll();

        List<Professeur> professeursFiltre = filtrerListeParSessionActuelle(professeurs);
        if (professeursFiltre.isEmpty())
            return 0L;

        long nombreEleveTotal = professeursFiltre.stream()
                .mapToLong(p -> p.getEtudiants() != null ? p.getEtudiants().size() : 0L)
                .sum();

        return Math.round((double) nombreEleveTotal / professeursFiltre.size());
    }

    public Long countProfesseurInscritSessionActuelle() {
        List<Professeur> professeurs = professeurRepository.findAll();
        return (long) filtrerListeParSessionActuelle(professeurs).size();
    }

    public Long countProfesseurAvecEtudiantSessionActuelle() {
        List<Professeur> professeurs = professeurRepository.findByEtudiantsIsNotEmpty();
        return (long) filtrerListeParSessionActuelle(professeurs).size();
    }

    public Long countProfesseurSansEtudiantSessionActuelle() {
        List<Professeur> professeurs = professeurRepository.findByEtudiantsIsEmpty();
        return (long) filtrerListeParSessionActuelle(professeurs).size();
    }

    private List<Professeur> filtrerListeParSessionActuelle(List<Professeur> professeurs){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
      return sessionService.filtrerUtilisateursParSession(professeurs,sessionDTO.saison(),sessionDTO.annee());
    }
}