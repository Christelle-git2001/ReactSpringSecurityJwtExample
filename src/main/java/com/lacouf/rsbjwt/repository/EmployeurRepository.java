package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Employeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeurRepository extends JpaRepository<Employeur, Long> {
    @Query("SELECT e FROM Employeur e JOIN UserApp u ON e.id = u.id WHERE u.credentials.email = :email")
    Optional<Employeur> findByEmail(String email);
}
