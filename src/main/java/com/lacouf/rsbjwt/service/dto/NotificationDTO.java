package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.Notification;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationDTO(long id, String titre, String description, NotificationType notificationType, LocalDateTime dateNotifiee, String urlCible, boolean estLu, Map<VariableMapKey, String> variables) {

    public static NotificationDTO of(Notification notification){
        return new NotificationDTO(
                notification.getId(),
                notification.getTitre(),
                notification.getDescription(),
                notification.getNotificationType(),
                notification.getDateNotifiee(),
                notification.getUrlCible(),
                notification.isEstLu(),
                notification.getVariables()
        );
    }
}
