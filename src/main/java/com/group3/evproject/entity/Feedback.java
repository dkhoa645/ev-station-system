package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "feedback")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "user_id")
    User user;

    @ManyToOne @JoinColumn(name = "station_id")
    ChargingStation station;

    Integer rating;
    String comment;
    java.time.LocalDateTime createdAt;
}
