package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Employeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EmployeurRepository extends JpaRepository<Employeur, Long> {
    @Query("SELECT e FROM Employeur e WHERE e.credentials.email = :email")
    Optional<Employeur> rechercheParEmail(String email);

    @Query("SELECT COUNT(e) > 0 FROM Employeur e WHERE e.credentials.email = :email")
    boolean existeParEmail(@Param("email") String email);
}
