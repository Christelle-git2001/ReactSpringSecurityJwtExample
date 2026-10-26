
package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.ConvocationEntrevue;
import com.lacouf.rsbjwt.model.Enum.ModaliteEntrevue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record ConvocationEntrevueDTO(
        long id,
        LocalDateTime dateCreation,

        @NotNull
        ModaliteEntrevue modalite,

        @NotEmpty
        List<@NotNull @Valid PlageHoraireDTO> plagesHoraires
) {

        public static ConvocationEntrevueDTO of(
                ConvocationEntrevue convocation) {

                return new ConvocationEntrevueDTO(
                        convocation.getId(),
                        convocation.getDateCreation(),
                        convocation.getModalite(),
                        convocation.getPlagesHoraires()
                                .stream()
                                .map(plage -> new PlageHoraireDTO(
                                        plage.getDateHeureDebut(),
                                        plage.getDureeMinutes()
                                ))
                                .toList()
                );
        }
}
