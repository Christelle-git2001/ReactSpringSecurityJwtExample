package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DonneesEmployeurService {
    private final EmployeurRepository employeurRepository;
    private final SessionService sessionService;

    public DonneesEmployeurService(EmployeurRepository employeurRepository, SessionService sessionService) {
        this.employeurRepository = employeurRepository;
        this.sessionService = sessionService;
    }

    public Long compterEmployeurSessionActuelle(){
        List<Employeur> employeurs = employeurRepository.findAll();
        return (long) filtrerListeParSessionActuelle(employeurs).size();
    }

    private List<Employeur> filtrerListeParSessionActuelle(List<Employeur> employeurs){
        SessionDTO sessionDTO = sessionService.getSessionActuelle();
        return sessionService.filtrerUtilisateursParSession(employeurs,sessionDTO.saison(),sessionDTO.annee());
    }
}
