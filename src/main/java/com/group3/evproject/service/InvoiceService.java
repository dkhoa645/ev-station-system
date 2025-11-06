package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.InvoiceRepository;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
        return invoiceRepository.findBySession_Booking_User_Id(userId);
    }

    public List<Invoice> getInvoicesByVehicle(Long vehicleId) {
        return invoiceRepository.findBySession_Booking_Vehicle_Id(vehicleId);
    }

    public List<Invoice> getPedingInvoices (String status) {
        return invoiceRepository.findByStatus(Invoice.Status.PENDING);
    }

    @Transactional
    public Invoice createInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_EXISTS,"Invoice"));

        //tìm payment theo user
        Payment payment = paymentService.findByUser(invoice.getSession().getBooking().getUser());

        //cập nhật tổng chi phí
        BigDecimal baseCost = invoice.getFinalCost() != null ? invoice.getFinalCost() : BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.ONE;
        if (invoice.getSubscriptionPlan() != null && invoice.getSubscriptionPlan().getMultiplier() != null) {
            multiplier = invoice.getSubscriptionPlan().getMultiplier();
        }
        BigDecimal discountFinalCost = baseCost.multiply(multiplier);
        invoice.setFinalCost(discountFinalCost);

        //payment  vào invoice, add invoice vào payment
        invoice.setPayment(payment);
        payment.getInvoices().add(invoice);

        //cap nhat tong hoa don
        payment.setTotalCost(payment.getTotalCost().add(discountFinalCost));
        payment.setTotalEnergy(payment.getTotalEnergy().add(BigDecimal.valueOf(invoice.getSession().getEnergyUsed())));
        payment.setStatus(PaymentStatus.UNPAID);

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
