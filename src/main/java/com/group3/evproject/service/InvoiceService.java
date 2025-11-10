package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.entity.*;
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
import java.math.RoundingMode;
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

    public Invoice createInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Invoice"));

        //tìm payment theo user
        Payment payment = paymentService.findByUser(invoice.getSession().getBooking().getUser());

        //payment  vào invoice, add invoice vào payment
        invoice.setPayment(payment);
        payment.getInvoices().add(invoice);

        //cập nhật tổng chi phí
        BigDecimal totalCost = payment.getTotalCost().add(invoice.getFinalCost());
        payment.setTotalCost(totalCost);
        payment.setTotalEnergy(payment.getTotalEnergy()
                .add(BigDecimal.valueOf(invoice.getSession().getEnergyUsed())));
        payment.setStatus(PaymentStatus.UNPAID);
        paymentService.save(payment);
        return invoice;
    }

    @Transactional
    public Invoice createInvoiceBySessionId(Long sessionId) {
        ChargingSession session = chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found with ID: " + sessionId));

        // Kiểm tra xem invoice đã tồn tại chưa
        if (invoiceRepository.findBySession_Id(sessionId).isPresent()) {
            throw new IllegalStateException("Invoice already exists for this session.");
        }

        Vehicle vehicle = session.getBooking() != null ? session.getBooking().getVehicle() : null;
        SubscriptionPlan plan = null;

        //Lấy SubscriptionPlan nếu xe đang có gói đang hoạt động
        if (vehicle != null && vehicle.getSubscription() != null) {
            VehicleSubscription vehicleSub = vehicle.getSubscription();
            boolean isActive = vehicleSub.getStatus() == VehicleSubscriptionStatus.ACTIVE &&
                    (vehicleSub.getEndDate() == null || vehicleSub.getEndDate().isAfter(LocalDateTime.now()));

            if (isActive) {
                plan = vehicleSub.getSubscriptionPlan();
            }
        }

        //ính chi phí
        BigDecimal baseCost = BigDecimal.valueOf(session.getTotalCost() != null ? session.getTotalCost() : 0.0);
        BigDecimal multiplier = (plan != null && plan.getMultiplier() != null)
                ? plan.getMultiplier()
                : BigDecimal.ONE;

        BigDecimal finalCost = baseCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        //Tạo invoice mới
        Invoice invoice = Invoice.builder()
                .session(session)
                .subscriptionPlan(plan)
                .issueDate(LocalDateTime.now())
                .finalCost(finalCost)
                .status(Invoice.Status.PENDING)
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        return saved;
    }

    // Xóa hóa đơn
    public void deleteInvoice(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new EntityNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }

}
