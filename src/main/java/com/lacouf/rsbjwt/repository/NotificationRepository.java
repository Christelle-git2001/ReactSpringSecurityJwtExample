package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
