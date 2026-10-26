
package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.ModaliteEntrevue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreationConvocationDTO(
        @NotNull Long candidatureId,
        @NotNull ModaliteEntrevue modalite,
        @NotEmpty List<@NotNull @Valid PlageHoraireDTO> plagesHoraires
) {
}
