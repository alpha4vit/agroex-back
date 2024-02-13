package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "creation_date")
    @CurrentTimestamp(source = SourceType.VM, event = EventType.INSERT)
    private Instant creationDate;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "avatar")
    private String avatar;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LotEntity> lots;

    @Column(name = "time_zone")
    private ZoneId timeZone;

}
