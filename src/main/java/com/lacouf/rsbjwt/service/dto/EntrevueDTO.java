
package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Entrevue;
import com.lacouf.rsbjwt.model.Enum.FormatEntrevue;

import java.time.LocalDateTime;

public record EntrevueDTO(
        long id,
        LocalDateTime dateCreation,
        FormatEntrevue format,
        LocalDateTime dateHeureEntrevue
) {

        public static EntrevueDTO of(Entrevue entrevue) {
                return new EntrevueDTO(
                        entrevue.getId(),
                        entrevue.getDateCreation(),
                        entrevue.getFormat(),
                        entrevue.getDateHeureEntrevue()
                );
        }
}
