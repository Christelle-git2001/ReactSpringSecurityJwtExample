package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.NotificationType;
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
    @JoinColumn(name = "user_app_id", nullable = false)
    private UserApp destinataire;
    @Column(nullable = false)
    private boolean estLu;

    @Builder
    public Notification(String titre, String description, NotificationType notificationType,LocalDateTime dateCreaction,String urlCible,UserApp destinataire ){
        this.titre = titre;
        this.description = description;
        this.notificationType = notificationType;
        this.dateNotifiee = dateCreaction;
        this.urlCible = urlCible;
        this.destinataire = destinataire;
        this.estLu = false;
    }
}
