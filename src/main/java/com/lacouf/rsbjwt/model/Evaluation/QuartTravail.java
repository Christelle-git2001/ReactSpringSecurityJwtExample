package com.lacouf.rsbjwt.model.Evaluation;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuartTravail {
    private String heureDebut;
    private String heureFin;
}
