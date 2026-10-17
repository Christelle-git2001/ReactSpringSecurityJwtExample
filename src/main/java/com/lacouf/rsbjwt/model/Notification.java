package com.lacouf.rsbjwt.model;

import com.lacouf.rsbjwt.model.Enum.Notification.NotificationType;
import com.lacouf.rsbjwt.model.Enum.Notification.VariableMapKey;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.validator.ValidNotificationRecipient;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    //@Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<VariableMapKey, String> variables = new HashMap<>();

    @Builder
    public Notification(String titre, String description, NotificationType notificationType, LocalDateTime dateCreaction, String urlCible, UserApp destinataire, Role role, Map<VariableMapKey, String> variables) {
        this.titre = titre;
        this.description = description;
        this.notificationType = notificationType;
        this.dateNotifiee = dateCreaction;
        this.urlCible = urlCible;
        this.destinataire = destinataire;
        this.estLu = false;
        this.role = role;
        if (variables != null) {
            this.variables = variables;
        }
    }
}
