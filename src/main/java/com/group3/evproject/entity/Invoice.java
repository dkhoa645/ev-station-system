package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "session_id")
    ChargingSession session;

    @Column(name = "issue_date", nullable = false)
    LocalDateTime issueDate;

    @Column(name = "final_cost", nullable = false)
    Double finalCost;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    Payment payment;
}
