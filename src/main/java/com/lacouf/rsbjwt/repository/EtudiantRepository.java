package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByMatricule(String matricule);
    Optional<Etudiant> findByCredentialsEmail(String email);
    List<Etudiant> findAllByDepartmentAndCv_Statut( Departement department, Statut statut );
}
