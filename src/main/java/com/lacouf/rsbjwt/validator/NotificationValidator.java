package com.lacouf.rsbjwt.validator;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.auth.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotificationValidator implements ConstraintValidator<ValidNotificationRecipient, Notification> {

    @Override
    public boolean isValid(Notification notification, ConstraintValidatorContext context) {
        if (notification == null) {
            return true;
        }

        Role role = notification.getRole();
        var destinataire = notification.getDestinataire();

        boolean isGestionnaire = (role == Role.GESTIONNAIRE);

        return isGestionnaire || destinataire != null;
    }
}