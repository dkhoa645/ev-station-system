package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "charging_spot")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    // Tên của chỗ sạc
    @Column(nullable = false)
    String name;

    // Trạng thái: AVAILABLE, IN_USE, MAINTENANCE, ...
    @Column(nullable = false)
    private String status;

    // Công suất đầu ra của chỗ sạc (kW)
    @Column(nullable = false)
    Double powerOutput;

    // Mỗi chỗ sạc thuộc về 1 trạm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    ChargingStation station;
}

