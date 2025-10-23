package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "charging_session")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    ChargingStation station;

    @ManyToOne
    @JoinColumn(name = "spot_id", nullable = false)
    ChargingSpot spot;

    @Column(name = "energy_used")
    Double energyUsed;

    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Column(name = "total_cost")
    Double totalCost;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    @Column(name = "battery_start")
    private Double batteryStart;

    @Column(name = "battery_end")
    private Double batteryEnd;

    @Column(name = "charging_duration")
    private Double chargingDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SessionStatus status = SessionStatus.IN_PROGRESS;

    public enum SessionStatus {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}

