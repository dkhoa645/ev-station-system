package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @JsonIgnore
    private ChargingStation station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id",nullable = false)
    @JsonIgnore
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id")
    @JsonIgnore
    private ChargingSpot spot;

    @Column(name = "time_to_charge", nullable = true)
    LocalDateTime timeToCharge;

    // Thời điểm hệ thống ghi nhận booking
    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_cost")
    Double totalCost;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED,
        CHARGING
    }
    @PrePersist
    protected void onCreate() {
        // Gán thời gian hiện tại cho startTime nếu chưa có
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
        // Nếu người dùng không truyền timeToCharge, thì mặc định = startTime
        if (this.timeToCharge == null) {
            this.timeToCharge = this.startTime;
        }
        // Trạng thái mặc định
        if (this.status == null) {
            this.status = BookingStatus.PENDING;
        }
    }
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions = new ArrayList<>();
}
