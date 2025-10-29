package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name="issue_date")
    LocalDateTime issueDate;

    @Column(name="final_cost")
    BigDecimal finalCost;

    @OneToOne()
    @JoinColumn(name="session_id")
    @JsonBackReference
    ChargingSession chargingSession;

    @ManyToOne()
    @JoinColumn(name="payment_id")
    Payment payment;


}
