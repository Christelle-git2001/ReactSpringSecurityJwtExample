package com.lacouf.rsbjwt.model;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "offre_de_stage_id", nullable = false)
    private OffreDeStage offreDeStage;

    @Column(nullable = false)
    private LocalDateTime dateCandidature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut = StatutCandidature.SOUMISE;

    @Column(length = 1000)
    private String motifRefus;

    public boolean occupeUnePlace() {
        return statut == StatutCandidature.ACCEPTEE
                || statut == StatutCandidature.STAGE_TROUVE
                || statut == StatutCandidature.CONTRAT_CREE;
    }

    public boolean actionsDesactivees() {
        if (offreDeStage != null && !occupeUnePlace() && offreDeStage.estComplete()) {
            return true;
        }
        if (etudiant == null) return false;
        Candidature stageChoisi = etudiant.getCandidatureStage();
        if (stageChoisi != null) {
            return !java.util.Objects.equals(stageChoisi.getId(), id);
        }
        return etudiant.getStatut() == com.lacouf.rsbjwt.model.Enum.StatutEtudiant.STAGE_TROUVE
                && statut != StatutCandidature.STAGE_TROUVE
                && statut != StatutCandidature.CONTRAT_CREE;
    }

    @Builder
    public Candidature(Etudiant etudiant, OffreDeStage offreDeStage, LocalDateTime dateCandidature) {
        this.etudiant = etudiant;
        this.offreDeStage = offreDeStage;
        this.dateCandidature = dateCandidature != null ? dateCandidature : LocalDateTime.now();
    }
}
