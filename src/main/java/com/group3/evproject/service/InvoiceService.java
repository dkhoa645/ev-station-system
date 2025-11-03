package com.group3.evproject.service;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.InvoiceRepository;
import com.group3.evproject.repository.PaymentRepository;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentRepository paymentRepository;

    // Lấy tất cả hóa đơn
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Lấy hóa đơn theo ID
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
    }

    // Tạo mới hóa đơn
    public Invoice createInvoice(Invoice invoice) {

        if (invoice.getSession() == null || invoice.getSession().getId() == null) {
            throw new IllegalArgumentException("Sesion ID must be provied");
        }

        if (invoice.getSubscriptionPlan() == null || invoice.getSubscriptionPlan().getId() == null) {
            throw new IllegalArgumentException("Subscription Plan ID must be provied");
        }

        //lấy dữ liệu từ db
        ChargingSession session = chargingSessionRepository.findById(invoice.getSession().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session not found with id: " + invoice.getSession().getId()
                ));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(invoice.getSubscriptionPlan().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subscription plan not found with id: " + invoice.getSubscriptionPlan().getId()
                ));

        //tính final cost
        BigDecimal finalCost = BigDecimal.ZERO;
        if (session.getTotalCost() != null && plan.getMultiplier() != null) {
            finalCost = BigDecimal.valueOf(session.getTotalCost()).multiply(plan.getMultiplier()).setScale(2, RoundingMode.HALF_UP);
        }

        //set các giá trị còn thiếu
        invoice.setSession(session);
        invoice.setSubscriptionPlan(plan);
        invoice.setFinalCost(finalCost);
        invoice.setIssueDate(invoice.getIssueDate() != null ? invoice.getIssueDate() : LocalDateTime.now());
        invoice.setStatus(invoice.getStatus() != null ? invoice.getStatus() : Invoice.Status.PENDING);

        return invoiceRepository.save(invoice);
    }

    // Xóa hóa đơn
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    //hủy hóa đơn
    public Invoice cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + invoiceId));

        invoice.setStatus(Invoice.Status.CANCELLED);
        return invoiceRepository.save(invoice);
    }

    //đánh dấu hóa đơn đã thanh toán
    public Invoice markAsPaid(Long invoiceId, Long paymentId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + invoiceId));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + paymentId));

        invoice.setPayment(payment);
        invoice.setStatus(Invoice.Status.PAID);

        return invoiceRepository.save(invoice);
    }
}
