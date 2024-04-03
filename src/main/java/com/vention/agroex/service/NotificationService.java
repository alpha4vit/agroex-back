package com.vention.agroex.service;

import com.vention.agroex.dto.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface NotificationService {

    Notification save(Notification notification);

    List<Notification> getAll();

    Notification getById(UUID notificationId);
}
