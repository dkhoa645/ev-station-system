package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.InvoiceRepository;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static java.rmi.server.LogStream.log;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentService paymentService;

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceBySessionId(Long sessionId) {
        return invoiceRepository.findBySession_Id(sessionId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for session id: " + sessionId));
    }

    public List<Invoice> getInvoicesByUserId(Long userId) {
        return invoiceRepository.findBySession_Booking_User_Id(userId);
    }

    public List<Invoice> getInvoicesByVehicle(Long vehicleId) {
        return invoiceRepository.findBySession_Booking_Vehicle_Id(vehicleId);
    }

    public List<Invoice> getPedingInvoices (String status) {
        return invoiceRepository.findByStatus(Invoice.Status.PENDING);
    }

    public Invoice createInvoice(Invoice invoice) {

        ChargingSession session = chargingSessionRepository.findById(invoice.getSession().getId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        invoice.setSession(session);

        //tìm payment theo user
        Payment payment = paymentService.findByUser(invoice.getSession().getBooking().getUser());
        if (payment == null) {
            throw  new EntityNotFoundException("Payment record not found for user: " + session.getBooking().getUser().getId());
        }

        SubscriptionPlan plan = null;
        if (invoice.getSubscriptionPlan() != null && invoice.getSubscriptionPlan().getId() != null) {
            plan = subscriptionPlanRepository.findById(invoice.getSubscriptionPlan().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Subscription plan not found with id: " + invoice.getSubscriptionPlan().getId()));
            invoice.setSubscriptionPlan(plan);
            System.out.println("Plan:" + plan);
        }else{
            System.out.println("plan == null");
        }

        //payment  vào invoice, add invoice vào payment
        invoice.setPayment(payment);
        payment.getInvoices().add(invoice);

        //tính finalCost
        Double baseCost = invoice.getSession().getTotalCost();
        Double finalCost = baseCost;

        if (plan != null) {
            double discount = (plan.getDiscount() != null) ? plan.getDiscount().doubleValue() : 0.0;
            double multiplier = (plan.getMultiplier() != null) ? plan.getMultiplier().doubleValue() : 1.0;

            // Nếu có discount, giảm giá trước
            if (discount > 0) {
                finalCost = finalCost * (1 - discount / 100);
            }

            // Áp dụng multiplier (ví dụ: 0.64)
            if (multiplier > 0) {
                finalCost = finalCost * multiplier;
            }
        }else{
            System.out.println( "not plan");
        }
        finalCost = Math.round(finalCost * 100.0) / 100.0;
        invoice.setFinalCost(BigDecimal.valueOf(finalCost));

        //ngay xuat hoa don
        if (invoice.getIssueDate() == null) {
            invoice.setIssueDate(LocalDateTime.now());
        }

        //tinh trang hoa don
        if (invoice.getStatus() == null) {
            invoice.setStatus(Invoice.Status.PENDING);
        }
        invoiceRepository.save(invoice);
        paymentService.save(payment);


        return invoice;
    }

    // Xóa hóa đơn
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

}
