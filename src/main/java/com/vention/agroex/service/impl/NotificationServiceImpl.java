package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Notification;
import com.vention.agroex.repository.NotificationRepository;
import com.vention.agroex.service.NotificationService;
import com.vention.agroex.util.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        var notificationEntity = mapper.toEntity(notification);
        return mapper.toDTO(repository.save(notificationEntity));
    }


    @Override
    public List<Notification> getAll() {
        return mapper.toDTOs(repository.findAll());
    }

    @Override
    public Notification getById(UUID notificationId) {
        return mapper.toDTO(repository.findById(notificationId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("There is no notifications with id: %s", notificationId))
                ));
    }
}
