
package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.FormatEntrevue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreationEntrevueDTO(
        @NotNull Long candidatureId,
        @NotNull FormatEntrevue format,
        @NotNull LocalDateTime dateHeureEntrevue,
        @NotNull int duree
) {
}
