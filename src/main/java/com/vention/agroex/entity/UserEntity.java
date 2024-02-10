package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @Column(name = "avatar")
    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<LotEntity> lots;

}
