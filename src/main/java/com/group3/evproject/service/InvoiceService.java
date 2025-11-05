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
        return invoiceRepository.findByUserId(userId);
    }

    public List<Invoice> getInvoicesByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    public Invoice createInvoice(Invoice invoice) {

        //tìm payment theo user
        Payment payment = paymentService.findByUser(invoice.getSession().getBooking().getUser());

        //payment  vào invoice, add invoice vào payment
        invoice.setPayment(payment);
        payment.getInvoices().add(invoice);

        //cập nhật tổng chi phí
        BigDecimal totalCost = payment.getTotalCost().add(invoice.getFinalCost());
        payment.setTotalCost(totalCost);

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
