package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Evaluation.SuperviseurInfo;
import lombok.Builder;

@Builder
public record SuperviseurStageDTO(
        String firstName,
        String lastName,
        String fonction,
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