package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "booking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "user_id")
    User user;

    @ManyToOne @JoinColumn(name = "station_id")
    ChargingStation station;

    @ManyToOne @JoinColumn(name = "vehicle_id")
    Vehicle vehicle;

    @ManyToOne @JoinColumn(name = "spot_id")
    ChargingSpot spot;

    java.time.LocalDateTime startTime;
    java.time.LocalDateTime endTime;
    String status; // booked, cancelled, completed
}

