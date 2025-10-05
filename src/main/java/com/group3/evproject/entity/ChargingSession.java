package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "charging_session")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "booking_id")
    Booking booking;

    @ManyToOne @JoinColumn(name = "spot_id")
    ChargingSpot spot;

    java.time.LocalDateTime startTime;
    java.time.LocalDateTime endTime;
    String status; // ongoing, finished, stopped
}

