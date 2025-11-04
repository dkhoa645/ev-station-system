package com.group3.evproject.service;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.Invoice;
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

        double finalCost = 0.0;
        if (session.getTotalCost() != null && plan.getMultiplier() != null) {
            BigDecimal totalCost = BigDecimal.valueOf(session.getTotalCost());
            BigDecimal multiplier = plan.getMultiplier();

            // phép nhân BigDecimal
            finalCost = totalCost.multiply(multiplier).doubleValue();
        }

        invoice.setFinalCost(BigDecimal.valueOf(Double.valueOf(finalCost)));

        invoice.setSession(session);
        invoice.setSubscriptionPlan(plan);

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
}
