package com.group3.evproject.service;


import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.dto.request.PaymentCreationRequest;
import com.group3.evproject.dto.response.CompanyPaymentDetailResponse;
import com.group3.evproject.dto.response.DriverInvoiceSummaryResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.dto.response.PaymentResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Invoice;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.PaymentMapper;
import com.group3.evproject.repository.CompanyRepository;
import com.group3.evproject.repository.PaymentRepository;
import com.group3.evproject.utils.UserUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    UserService userService;
    CompanyRepository companyRepository;
    UserUtils userUtils;


    public Payment save(Payment payment) {
        return
                paymentRepository.save(payment);
    }

    public Payment findById(Long id) {
        return paymentRepository.findByid(id);
    }

    public Payment createNew(User user, Company company) {
        LocalDateTime period = getPeriod();
        return Payment.builder()
                .user(user)
                .company(company)
                .totalEnergy(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .paidCost(BigDecimal.ZERO)
                .status(PaymentStatus.UNPAID)
                .period(period)
                .invoices(new ArrayList<>())
                .paymentTransactions(new ArrayList<>())
                .build();
    }


    public PaymentResponse createPayment(PaymentCreationRequest request) {
        Payment payment = null;
        //Check user có gói chưa
        if (request.getUserId() != null) {
            User user = userService.findById(request.getUserId());
            if (!user.getPayments().stream().filter(paymentCheck ->
                    paymentCheck.getPeriod().equals(getPeriod())).findFirst().isEmpty()) {
                payment = createNew(user, null);
            } else {
                throw new AppException(ErrorCode.RESOURCES_EXISTS, "Payment");
            }
        }
        //Check company
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId()).orElse(null);
            if (company.getPayment().stream().filter(paymentCheck ->
                    paymentCheck.getPeriod().equals(getPeriod())).findFirst().isEmpty()) {
                payment = createNew(null, company);
            } else {
                throw new AppException(ErrorCode.RESOURCES_EXISTS, "Payment");
            }
        }
        if (payment == null) throw new RuntimeException("Payment must not be null");

        Payment saved = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(saved);
    }


    public Payment findByUser(User user) {
        final LocalDateTime finalPeriod = getPeriod();
        Payment payment = null;
        Company company = user.getCompany();
        if (company != null) {
            payment = paymentRepository.findByCompanyAndPeriod(company, finalPeriod);
            if (payment == null) payment = createNew(null, company);
        } else {
            payment = paymentRepository.findByUserAndPeriod(user, finalPeriod);
            if (payment == null) payment = createNew(user, null);
        }
        return payment;
    }

    public LocalDateTime getPeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime period = LocalDateTime.of(now.getYear(), now.getMonth(), 25, 0, 0);
        if (now.getDayOfMonth() > 25) {
            period = period.plusMonths(1);
        }
        return period;
    }

    public List<PaymentDetailResponse> getAll() {
        return paymentRepository.findAll()
                .stream().map(paymentMapper::toPaymentDetailResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailResponse> getCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "Company"));
        return paymentRepository.findByCompany(company).stream()
                .map(paymentMapper::toPaymentDetailResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailResponse> getUser(Long id) {
        User user = userService.findById(id);
        return paymentRepository.findByUser(user).stream()
                .map(paymentMapper::toPaymentDetailResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailResponse> getForCompany() {
        User user = userUtils.getCurrentUser();
        return paymentRepository.findByCompany(user.getCompany()).stream()
                .map(paymentMapper::toPaymentDetailResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailResponse> getForUser() {
        User user = userUtils.getCurrentUser();
        return paymentRepository.findByUser(user)
                .stream()
                .map(paymentMapper::toPaymentDetailResponse)
                .collect(Collectors.toList());
    }

    public Payment processPayment(Payment payment, BigDecimal amount) {
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment is already paid");
        }
        //cập nhật số tiền đã trả
        BigDecimal newPaidCost = payment.getPaidCost().add(amount);
        payment.setPaidCost(newPaidCost);

        //trả đủ -> paid
        if (newPaidCost.compareTo(payment.getTotalCost()) >= 0) {
            payment.setStatus(PaymentStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.UNPAID);
        }
        return paymentRepository.save(payment);
    }

    public List<CompanyPaymentDetailResponse> getDetailForCompany() {
        List<CompanyPaymentDetailResponse> list = new ArrayList<>();
        Map<String, DriverInvoiceSummaryResponse> detailMap = new HashMap<>();

        User user = userUtils.getCurrentUser();
        List<Payment> payments = paymentRepository.findByCompany(user.getCompany());
//        Mỗi payment 1 period khác nhau, mỗi period gồm nhiều invoice
        for (Payment payment : payments) {
            CompanyPaymentDetailResponse companyPaymentDetailResponse = paymentMapper.toCompanyPaymentDetailResponse(payment);
            for (Invoice invoice : payment.getInvoices()) {
                User driver = invoice.getSession().getBooking().getUser();
                detailMap.compute(driver.getName(), (k, v) -> {
                    if (v == null) {
                        return DriverInvoiceSummaryResponse.builder()
                                .id(driver.getId())
                                .totalCost(invoice.getFinalCost())
                                .invoiceCount(1L)
                                .build();
                    } else {
                        v.setTotalCost(v.getTotalCost().add(invoice.getFinalCost()));
                        v.setInvoiceCount(v.getInvoiceCount() + 1);
                        companyPaymentDetailResponse.setInvoiceCount(v.getInvoiceCount());
                        return v;
                    }
                });}
            companyPaymentDetailResponse.setDriverDetails(detailMap);

            list.add(companyPaymentDetailResponse);
        }
        return list;
    }
}
