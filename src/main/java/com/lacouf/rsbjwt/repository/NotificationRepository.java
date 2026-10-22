package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.UserApp;
import com.lacouf.rsbjwt.model.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> getNotificationByRole(Role role);

    List<Notification> getNotificationByDestinataire(UserApp destinataire);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.role = :role")
    Long totalByRole(Role role);
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.destinataire = :userApp")
    Long totalByUser(UserApp userApp);

    void deleteByUrlCible(String urlCible);

    void deleteNotificationByNotificationTypeAndDestinataire(NotificationType notificationType, UserApp destinataire);

    Optional<Notification> findByNotificationTypeAndUrlCible(
            NotificationType notificationType,
            String urlCible
    );
}