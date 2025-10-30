package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "charging_spot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String spotName;

    @Column(name = "power_output")
    private Double powerOutput;

    //Enum trạng thái thực tế của điểm sạc
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status = SpotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonBackReference(value = "station-spots")
    private ChargingStation station;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", nullable = false)
    private SpotType spotType;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "spot-bookings")
    List<Booking> bookings;

    public enum SpotType {
        WALK_IN, // không cần đặt trước
        BOOKING  // chỉ dành cho người đã đặt
    }

    public enum SpotStatus {
        AVAILABLE,  // sẵn sàng
        OCCUPIED,   // đang được sử dụng
        MAINTENANCE // đang bảo trì
    }
}
