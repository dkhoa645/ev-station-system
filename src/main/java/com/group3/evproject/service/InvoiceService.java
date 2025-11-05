package com.group3.evproject.service;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.InvoiceRepository;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        //payment  vào invoice, add invoice vào payment
        invoice.setPayment(payment);
        payment.getInvoices().add(invoice);

        //tính finalCost
        BigDecimal baseCost = BigDecimal.valueOf(invoice.getSession().getTotalCost());
        BigDecimal multiplier = BigDecimal.ONE;

        if (invoice.getSubscriptionPlan() != null && invoice.getSubscriptionPlan().getDiscount() != null) {
            BigDecimal discount = invoice.getSubscriptionPlan().getDiscount();
            multiplier = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100)));
        }

        BigDecimal calculatedFinalCost = baseCost.multiply( multiplier);
        invoice.setFinalCost(calculatedFinalCost);

        if (payment.getTotalCost() == null) {
            payment.setTotalCost(BigDecimal.ZERO);
        }

        //cong them vao payment
        payment.setTotalCost(payment.getTotalCost().add(calculatedFinalCost));

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

        System.out.println("   Created invoice for session " + session.getId());
        System.out.println("   Base cost: " + baseCost);
        System.out.println("   Final cost (after discount): " + calculatedFinalCost);
        System.out.println("   Payment total cost: " + payment.getTotalCost());

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
