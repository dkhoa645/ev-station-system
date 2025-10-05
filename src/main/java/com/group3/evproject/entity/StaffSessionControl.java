package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "staff_session_control")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffSessionControl {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "staff_id")
    User staff;

    @ManyToOne @JoinColumn(name = "session_id")
    ChargingSession session;

    String action; // start, stop, confirm_payment
    java.time.LocalDateTime timestamp;
}
