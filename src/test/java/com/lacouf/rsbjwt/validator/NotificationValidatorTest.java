package com.lacouf.rsbjwt.validator;

import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.model.UserApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationValidatorTest {

    private NotificationValidator validator;

    @BeforeEach
    void init() {
        validator = new NotificationValidator();
    }

    @Test
    void doitEtreValideSiNotificationEstNull() {
        boolean isValid = validator.isValid(null, null);
        assertThat(isValid).isTrue();
    }

    @Test
    void doitEtreValideSiRoleEstGestionnaireEtDestinataireNull() {
        Notification notification = new Notification();
        notification.setRole(Role.GESTIONNAIRE);
        notification.setDestinataire(null);

        boolean isValid = validator.isValid(notification, null);
        assertThat(isValid).isTrue();
    }

    @Test
    void doitEtreValideSiRoleEstGestionnaireEtDestinatairePresent() {
        Notification notification = new Notification();
        notification.setRole(Role.GESTIONNAIRE);
        notification.setDestinataire(Mockito.mock(UserApp.class));

        boolean isValid = validator.isValid(notification, null);
        assertThat(isValid).isTrue();
    }

    @Test
    void doitEtreValideSiRoleNestPasGestionnaireEtDestinatairePresent() {
        Notification notification = new Notification();
        notification.setRole(Role.ETUDIANT);
        notification.setDestinataire(Mockito.mock(UserApp.class));

        boolean isValid = validator.isValid(notification, null);
        assertThat(isValid).isTrue();
    }

    @Test
    void doitEtreInvalideSiRoleNestPasGestionnaireEtDestinataireNull() {
        Notification notification = new Notification();
        notification.setRole(Role.ETUDIANT);
        notification.setDestinataire(null);

        boolean isValid = validator.isValid(notification, null);
        assertThat(isValid).isFalse();
    }
}