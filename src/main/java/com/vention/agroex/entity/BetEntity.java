package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bet")
public class BetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    private LotEntity lot;

    private Float amount;

    @ManyToOne
    @ToString.Exclude
    private UserEntity user;

    @CreationTimestamp
    private Instant betTime;

}
