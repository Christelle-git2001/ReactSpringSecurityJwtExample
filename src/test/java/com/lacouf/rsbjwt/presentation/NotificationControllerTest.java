package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunRoleDefini;
import com.lacouf.rsbjwt.Exception.NotificationNonTrouvee;
import com.lacouf.rsbjwt.Exception.UtilisateurIntrouvableException;
import com.lacouf.rsbjwt.model.Enum.NotificationType;
import com.lacouf.rsbjwt.service.NotificationService;
import com.lacouf.rsbjwt.service.dto.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class NotificationControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private NotificationService notificationService;

    private MockMvc mockMvc;

    private JsonMapper jsonMapper;

    private NotificationDTO notificationDTO;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        jsonMapper = JsonMapper.builder().build();
        notificationDTO = new NotificationDTO(-8,"notification.titre","notification.description", NotificationType.CVATTENTEVALIDATION, LocalDateTime.now(),"etudiant/1/cv",false, Map.of("etudiant.name","Jean François Castonguay"));
    }

    @Test
    void doitRetourneeListeNotificationDtoStatusOk() throws Exception {
        List<NotificationDTO> notificationDTOList = List.of(notificationDTO);
        when(notificationService.recevoirNotifications(1L)).thenReturn(notificationDTOList);
        mockMvc.perform(get("/notification/utilisateur/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value(notificationDTO.titre()))
                .andExpect(jsonPath("$[0].notificationType").value(notificationDTO.notificationType().name()))
                .andExpect(jsonPath("$[0].variables.['etudiant.name']").value("Jean François Castonguay"));
    }

    @Test
    void doitRetournerNotificationDTOLueStatusOK() throws Exception {
        notificationDTO = new NotificationDTO(-8,"notification.titre","notification.description", NotificationType.CVATTENTEVALIDATION, LocalDateTime.now(),"etudiant/1/cv",true, Map.of("etudiant.name","Jean François Castonguay"));
        when(notificationService.setNotificationLue(1L)).thenReturn(notificationDTO);
        mockMvc.perform(put("/notification/1/lu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value(notificationDTO.titre()))
                .andExpect(jsonPath("$.notificationType").value(notificationDTO.notificationType().name()))
                .andExpect(jsonPath("$.variables.['etudiant.name']").value("Jean François Castonguay"))
                .andExpect(jsonPath("$.estLu").value(true));
    }

    @Test
    void doitRetournerNotificationDTOSupprimerStatusOK() throws Exception {
        when(notificationService.supprimerNotification(1L)).thenReturn(notificationDTO);
        mockMvc.perform(delete("/notification/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value(notificationDTO.titre()))
                .andExpect(jsonPath("$.notificationType").value(notificationDTO.notificationType().name()))
                .andExpect(jsonPath("$.variables.['etudiant.name']").value("Jean François Castonguay"))
                .andExpect(jsonPath("$.estLu").value(false));
    }

    @Test
    void doitRetournerNombreNotificationStatusOk() throws Exception {
        List<NotificationDTO> notificationDTOList = new java.util.ArrayList<>(List.of(notificationDTO));
        notificationDTO = new NotificationDTO(-8,"notification.titre","notification.description", NotificationType.CVATTENTEVALIDATION, LocalDateTime.now(),"etudiant/1/cv",false, Map.of("etudiant.name","Jean Moustache Castonguay"));
        notificationDTOList.add(notificationDTO);
        when(notificationService.compterNotifications(1L)).thenReturn((long) notificationDTOList.size());
        mockMvc.perform(get("/notification/utilisateur/1/compteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(notificationDTOList.size()));
    }
}
