package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.response.ChargingSessionSimpleResponse;
import com.group3.evproject.dto.response.InvoiceResponse;
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

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentService paymentService;

    // Lấy tất cả hóa đơn
    public List<InvoiceResponse> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();

        return invoices.stream().map(invoice -> {
            ChargingSession chargingSession = invoice.getSession();

            ChargingSessionSimpleResponse sessionSimpleResponse = null;
            if (chargingSession != null) {
                sessionSimpleResponse = ChargingSessionSimpleResponse.builder()
                        .sessionId(chargingSession.getId())
                        .stationName(chargingSession.getBooking() != null && chargingSession.getStation() != null ? chargingSession.getBooking().getStation().getName() : null)
                        .stationId(chargingSession.getBooking() != null && chargingSession.getStation() != null ? chargingSession.getBooking().getStation().getId() : null)
                        .spotName(chargingSession.getSpot() != null ? chargingSession.getSpot().getSpotName() : null)
                        .bookingId(chargingSession.getBooking() != null ? chargingSession.getBooking().getId() : null)
                        .startTime(chargingSession.getStartTime())
                        .status(chargingSession.getStatus() != null ? chargingSession.getStatus().name() : null)
                        .build();
            }
            return InvoiceResponse.builder()
                    .id(invoice.getId())
                    .issueDate(invoice.getIssueDate())
                    .finalCost(invoice.getFinalCost())
                    .status(invoice.getStatus().name())
                    .session(sessionSimpleResponse)
                    .build();
        }).toList();
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

        Vehicle vehicle = null;
        if (session.getBooking() != null && session.getBooking().getVehicle() != null) {
            // TH1: Có booking
            vehicle = session.getBooking().getVehicle();
        } else if (session.getVehicle() != null) {
            // TH2: Walk-in member (không booking)
            vehicle = session.getVehicle();
        }

        if (vehicle == null) {
            throw new IllegalStateException("No vehicle associated with this session.");
        }

        // --- Lấy SubscriptionPlan nếu có gói hoạt động ---
        SubscriptionPlan plan = null;
        VehicleSubscription vehicleSub = vehicle.getSubscription();
        if (vehicleSub != null &&
                vehicleSub.getStatus() == VehicleSubscriptionStatus.ACTIVE &&
                (vehicleSub.getEndDate() == null || vehicleSub.getEndDate().isAfter(LocalDateTime.now()))) {
            plan = vehicleSub.getSubscriptionPlan();
        }

        // --- Tính chi phí ---
        BigDecimal baseCost = BigDecimal.valueOf(session.getTotalCost() != null ? session.getTotalCost() : 0.0);
        BigDecimal multiplier = (plan != null && plan.getMultiplier() != null)
                ? plan.getMultiplier()
                : BigDecimal.ONE;

        BigDecimal finalCost = baseCost.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);

        // --- Tạo Invoice ---
        Invoice invoice = Invoice.builder()
                .session(session)
                .subscriptionPlan(plan)
                .issueDate(LocalDateTime.now())
                .finalCost(finalCost)
                .status(Invoice.Status.PENDING)
                .build();

        return invoiceRepository.save(invoice);
    }


    // Xóa hóa đơn
    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));

        ChargingSession session = invoice.getSession();
        if (session != null) {
            session.setInvoice(null);
            chargingSessionRepository.save(session);
        }

        Payment payment = invoice.getPayment();
        if (payment != null) {
            payment.getInvoices().remove(invoice);
        }

        invoiceRepository.delete(invoice);
    }

}
