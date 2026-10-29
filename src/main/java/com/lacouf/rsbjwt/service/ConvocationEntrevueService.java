package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.ConvocationIntrouvableException;
import com.lacouf.rsbjwt.Exception.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.model.Entrevue;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.EntrevueRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EntrevueDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ConvocationEntrevueService {

    private final EntrevueRepository convocationRepository;
    private final UserAppRepository userAppRepository;

    public ConvocationEntrevueService(
            EntrevueRepository convocationRepository,
            UserAppRepository userAppRepository
    ) {
        this.convocationRepository = convocationRepository;
        this.userAppRepository = userAppRepository;
    }

    @Transactional
    public EntrevueDTO obtenirConvocationEtudiant(
            Long convocationId,
            String email
    ) throws EtudiantIntrouvableException, ConvocationIntrouvableException {

        Etudiant etudiant = userAppRepository.findUserAppByEmail(email)
                .filter(Etudiant.class::isInstance)
                .map(Etudiant.class::cast)
                .orElseThrow(EtudiantIntrouvableException::new);

        Entrevue convocation = convocationRepository
                .findByIdAndCandidatureEtudiantId(
                        convocationId,
                        etudiant.getId()
                )
                .orElseThrow(ConvocationIntrouvableException::new);

        return EntrevueDTO.of(convocation);
    }

}
