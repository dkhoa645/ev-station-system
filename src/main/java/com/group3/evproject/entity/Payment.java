package com.group3.evproject.entity;

import com.group3.evproject.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    BigDecimal totalEnergy;
    BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    LocalDateTime paidAt;
    @Column(unique = true)
    LocalDateTime period;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<Invoice> invoices;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = true)
    User user;

    @ManyToOne()
    @JoinColumn(name = "company_id", nullable = true)
    Company company;


    public void addInvoice(Invoice invoice) {
        if (invoice == null) return;

        invoice.setPayment(this);

        this.invoices.add(invoice);

        if (invoice.getFinalCost() != null) {
            this.totalCost = this.totalCost.add(invoice.getFinalCost());
        }
        if (invoice.getSession().getEnergyUsed() != null) {
            this.totalEnergy = this.totalEnergy.add(BigDecimal.valueOf(invoice.getSession().getEnergyUsed()));
        }
    }
}
