package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.NotificationType;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.validator.ValidNotificationRecipient;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ValidNotificationRecipient
public class Notification {
    @Id
    @GeneratedValue
    private  Long id;
    @Column(nullable = false)
    private String titre;
    @Column(nullable = false)
    private String description;
    @Enumerated
    @Column(nullable = false)
    private NotificationType notificationType;
    @Column(nullable = false)
    private LocalDateTime dateNotifiee;
    @Column(nullable = false)
    private String urlCible;
    @ManyToOne
    @JoinColumn(name = "user_app_id")
    private UserApp destinataire;
    @Column
    private Role role;
    @Column(nullable = false)
    private boolean estLu;

    @Builder
    public Notification(String titre, String description, NotificationType notificationType,LocalDateTime dateCreaction,String urlCible,UserApp destinataire, Role role ){
        this.titre = titre;
        this.description = description;
        this.notificationType = notificationType;
        this.dateNotifiee = dateCreaction;
        this.urlCible = urlCible;
        this.destinataire = destinataire;
        this.estLu = false;
        this.role = role;
    }
}
