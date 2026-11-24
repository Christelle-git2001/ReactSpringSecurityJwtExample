package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.repository.EmployeurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DonneesEmployeurService {
    private final EmployeurRepository employeurRepository;

    public DonneesEmployeurService(EmployeurRepository employeurRepository) {
        this.employeurRepository = employeurRepository;
    }

    public Long compterEmployeur(){
        return (long) employeurRepository.findAll().size();
    }
}
