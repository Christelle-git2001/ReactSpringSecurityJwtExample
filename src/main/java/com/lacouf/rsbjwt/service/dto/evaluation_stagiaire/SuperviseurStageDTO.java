package com.lacouf.rsbjwt.service.dto.evaluation_stagiaire;

import com.lacouf.rsbjwt.model.Evaluation.SuperviseurInfo;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Builder
public record SuperviseurStageDTO(
        String firstName,
        String lastName,
        String fonction,
        @NotBlank(message = "validation.telephone.required")
        @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "error.phone_invalid")
        String phone
) {

    public static SuperviseurInfo toEntity(SuperviseurStageDTO dto) {
        if (dto == null) return null;
        return SuperviseurInfo.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .fonction(dto.fonction())
                .phone(dto.phone())
                .build();
    }

    public static SuperviseurStageDTO toDto(SuperviseurInfo entity) {
        if (entity == null) return null;
        return new SuperviseurStageDTO(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getFonction(),
                entity.getPhone()
        );
    }
}