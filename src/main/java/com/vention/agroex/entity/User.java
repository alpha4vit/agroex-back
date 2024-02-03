package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

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

    @Column(name = "creation_date")
    @CreationTimestamp
    private Instant creationDate;

    @Column(name = "email_verified")
    private Boolean emailVerified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Lot> lots;

}
