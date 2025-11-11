package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @JsonBackReference
    Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @JsonBackReference
    ChargingStation station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    @JsonBackReference
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    @JsonBackReference
    ChargingSpot spot;

    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Column(name = "power_output") // công suất trạm sạc (kW)
    Double powerOutput;

    @Column(name = "charging_duration") // thời gian sạc (giờ)
    Double chargingDuration;

    @Column(name = "battery_capacity") // dung lượng pin (kWh)
    Double batteryCapacity;

    @Column(name = "percent_before") // % pin trước khi sạc
    Double percentBefore;

    @Column(name = "percent_after")
    Double percentAfter;

    @Column(name = "energy_added")
    Double energyAdded;

    @Column(name = "energy_used")
    Double energyUsed;

    @Column(name = "rate_per_kwh")
    Double ratePerKWh;

    @Column(name = "total_cost")
    Double totalCost;

    @OneToOne(mappedBy = "session",cascade = CascadeType.ALL)
    @JsonBackReference(value = "invoice-session")
    Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Status status = Status.ACTIVE;

    public enum Status {
        ACTIVE,
        COMPLETED,
        CANCELLED,
        ERROR
    }
}
