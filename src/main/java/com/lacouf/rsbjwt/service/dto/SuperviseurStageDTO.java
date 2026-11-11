package com.lacouf.rsbjwt.service.dto;

import lombok.Builder;

@Builder
public record SuperviseurStageDTO(String firstName, String lastName, String fonction, String phone) {
}
