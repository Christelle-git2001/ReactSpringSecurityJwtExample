
package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Session;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inscription_session",
        uniqueConstraints = @UniqueConstraint(name = "uk_utilisateur_session_annee", columnNames = {"utilisateur_id", "session", "annee_session"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class InscriptionSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UserApp utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Session session;

    @Column(name = "annee_session", nullable = false)
    private int anneeSession;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @OneToOne(mappedBy = "inscriptionSession")
    private CvEtudiant cv;

    public InscriptionSession(UserApp utilisateur, Session session, int anneeSession) {
        this.utilisateur = utilisateur;
        this.session = session;
        this.anneeSession = anneeSession;
        this.dateInscription = LocalDateTime.now();
    }
}
