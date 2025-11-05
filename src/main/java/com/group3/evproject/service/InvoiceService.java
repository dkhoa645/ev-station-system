package com.group3.evproject.service;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.InvoiceRepository;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    // Lấy hóa đơn theo ID
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
    }

    // Tạo mới hóa đơn
    public Invoice createInvoice(Invoice invoice) {
        ChargingSession session = chargingSessionRepository.findById(invoice.getSession().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session not found with id: " + invoice.getSession().getId()
                ));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(invoice.getSubscriptionPlan().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subscription plan not found with id: " + invoice.getSubscriptionPlan().getId()
                ));

        Double finalCost = 0.0;
        if (session.getTotalCost() != null && plan.getMultiplier() != null) {
            Double totalCost = Double.valueOf(session.getTotalCost());
            Double multiplier = Double.valueOf(String.valueOf((plan.getMultiplier())));

            // phép nhân BigDecimal
            finalCost = totalCost - (totalCost * multiplier) ;
        }

        invoice.setFinalCost(BigDecimal.valueOf(Double.valueOf(finalCost)));

        invoice.setSession(session);
        invoice.setSubscriptionPlan(plan);

        Payment payment = paymentService.findByUser(invoice.getSession().getBooking().getUser());

        List<Invoice> invoices = payment.getInvoices();
        invoices.add(invoice);
        payment.setInvoices(invoices);
        paymentService.save(payment);

        invoice.setPayment(payment);
        return invoiceRepository.save(invoice);
    }

    // Cập nhật hóa đơn
    public Invoice updateInvoice(Long id, Invoice newInvoice) {
        Invoice existing = getInvoiceById(id);
        existing.setFinalCost(newInvoice.getFinalCost());
        existing.setIssueDate(newInvoice.getIssueDate());
        existing.setSession(newInvoice.getSession());
        existing.setPayment(newInvoice.getPayment());
        return invoiceRepository.save(existing);
    }

    // Xóa hóa đơn
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

    public Invoice markAsPaid(Long invoiceId, Long paymentId) {
        Invoice invoice = getInvoiceById(invoiceId);

        if (invoice.getStatus() == Invoice.Status.CANCELLED) {
            throw new IllegalArgumentException("Cannot mark a cancelled invoice as paid.");
        }

        invoice.setStatus(Invoice.Status.PAID);

        // Nếu bạn có entity Payment thì liên kết nó vào
        Payment payment = null;
        if (paymentId != null) {
            payment = new Payment();
            payment.setId(paymentId);
            invoice.setPayment(payment);
        }

        return invoiceRepository.save(invoice);
    }

    public Invoice cancelInvoice(Long invoiceId) {
        Invoice invoice = getInvoiceById(invoiceId);

        if (invoice.getStatus() == Invoice.Status.PAID) {
            throw new IllegalArgumentException("Cannot cancel a paid invoice.");
        }

        invoice.setStatus(Invoice.Status.CANCELLED);
        return invoiceRepository.save(invoice);
    }

}
