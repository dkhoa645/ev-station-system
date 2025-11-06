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

    @Transactional
    public Invoice createInvoice(Long sessionId) {
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

    @Transactional
    public Invoice payInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + invoiceId));

        // Nếu đã thanh toán rồi thì không cho thanh toán lại
        if (invoice.getStatus() == Invoice.Status.PAID) {
            throw new IllegalStateException("Invoice has already been paid.");
        }

        // Xác định user hoặc company
        User user = null;
        Company company = null;
        if (invoice.getSession() != null && invoice.getSession().getBooking() != null) {
            user = invoice.getSession().getBooking().getUser();
            if (user != null) {
                company = user.getCompany();
            }
        }

        // Tìm hoặc tạo Payment tương ứng
        Payment payment;
        if (user != null) {
            payment = paymentService.findByUser(user);
        } else if (company != null) {
            payment = paymentService.createNew(null, company);
        } else {
            throw new IllegalStateException("Cannot determine user or company for this invoice.");
        }

        // Cập nhật thông tin thanh toán
        payment.getInvoices().add(invoice);
        payment.setTotalCost(payment.getTotalCost().add(invoice.getFinalCost()));
        payment.setPaidCost(payment.getPaidCost().add(invoice.getFinalCost()));

        // Nếu đủ điều kiện, cập nhật trạng thái payment
        payment.setStatus(PaymentStatus.PAID);
        paymentService.save(payment);

        // Cập nhật invoice
        invoice.setPayment(payment);
        invoice.setStatus(Invoice.Status.PAID);
        invoice.setPaymentDate(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }


}
