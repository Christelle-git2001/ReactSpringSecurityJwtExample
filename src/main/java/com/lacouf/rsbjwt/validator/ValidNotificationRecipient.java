package com.lacouf.rsbjwt.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotificationValidator.class)
@Documented
public @interface ValidNotificationRecipient {
    String message() default "Le destinataire est obligatoire si le rôle n'est pas gestionnaire.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}