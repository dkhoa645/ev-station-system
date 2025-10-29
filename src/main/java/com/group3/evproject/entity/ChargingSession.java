package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "charging_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Nếu Booking có thể null (trong trường hợp Walk-in), nên cho phép nullable = true
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @JsonBackReference
    Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference
    ChargingStation station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    @JsonBackReference
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
    Double batteryStart;

    @Column(name = "battery_end")
    Double batteryEnd;

    @Column(name = "charging_duration")
    Double chargingDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SessionStatus status = SessionStatus.IN_PROGRESS;

    public enum SessionStatus {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @OneToOne(mappedBy = "chargingSession")
    @JsonManagedReference
    private Invoice invoice;
}
