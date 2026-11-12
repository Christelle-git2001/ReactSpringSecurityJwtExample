package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Enum.StatutEtudiant;
import com.lacouf.rsbjwt.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByMatricule(String matricule);
    Optional<Etudiant> findByCredentialsEmail(String email);
    List<Etudiant> findAllByDepartmentAndCv_Statut( Departement department, Statut statut );
    List<Etudiant> findAllByProfesseurId(Long professeurId);

    long countByCvIsNull();
    long countByCandidatureStageIsNullAndStatutNot(StatutEtudiant statut);
    List<Etudiant> findByCvIsNull();
    List<Etudiant> findByCandidatureStageIsNullAndStatutNot(StatutEtudiant statut);
    long countByCandidatureStageIsNotNullOrStatut(StatutEtudiant statut);
    List<Etudiant> findByCandidatureStageIsNotNullOrStatut(StatutEtudiant statut);
    List<Etudiant> findByProfesseurIsNull();
    List<Etudiant> findByCvStatut(Statut statut);
    long countByDepartment(Departement department);

    @Query("SELECT e FROM Etudiant e WHERE NOT EXISTS " +
            "(SELECT entretien.id FROM Entrevue entretien WHERE entretien.candidature.etudiant = e)")
    List<Etudiant> findSansEntrevue();
}
