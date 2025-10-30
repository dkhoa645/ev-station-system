package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    ChargingStation station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnore
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    ChargingSpot spot;

    LocalDateTime timeToCharge;
    LocalDateTime startTime;
    LocalDateTime endTime;

    BigDecimal reservationFee;
    Double totalCost;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    BookingStatus status = BookingStatus.PENDING;

    public enum BookingStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED, CHARGING
    }

    @PrePersist
    protected void onCreate() {
        if (this.startTime == null) this.startTime = LocalDateTime.now();
        if (this.timeToCharge == null) this.timeToCharge = this.startTime;
        if (this.status == null) this.status = BookingStatus.PENDING;
    }

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions = new ArrayList<>();
}
