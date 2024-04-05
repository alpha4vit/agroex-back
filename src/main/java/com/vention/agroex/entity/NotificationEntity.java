package com.vention.agroex.entity;

import com.vention.agroex.util.constant.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class NotificationEntity {

    @Id
    private UUID id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private LotEntity lot;

    @ManyToOne
    @ToString.Exclude
    private UserEntity user;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @CreationTimestamp
    @Column(name = "send_time")
    private Instant sendTime;

    @Column(name = "read_status")
    private String readStatus;

    @Column(name = "role")
    private Role role;
}
