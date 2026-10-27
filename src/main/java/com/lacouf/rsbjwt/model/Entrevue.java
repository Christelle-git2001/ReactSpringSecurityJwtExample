
package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.FormatEntrevue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Entrevue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormatEntrevue format;

    @Column(nullable = false)
    private LocalDateTime dateHeureEntrevue;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "employeur_id")
    private Employeur employeur;
}
