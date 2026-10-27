
package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.ModaliteEntrevue;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConvocationEntrevue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModaliteEntrevue modalite;

    private String adresse;
    private String numeroBureau;
    private String lienVisioconference;
    private String plateformeReunion;

    @Column(length = 2000)
    private String instructionsComplementaires;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @OneToMany(
            mappedBy = "convocation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PlageHoraire> plagesHoraires = new ArrayList<>();

}
