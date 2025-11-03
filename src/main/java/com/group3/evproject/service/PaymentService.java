package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.dto.request.PaymentCreationRequest;
import com.group3.evproject.dto.response.PaymentResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.PaymentMapper;
import com.group3.evproject.repository.CompanyRepository;
import com.group3.evproject.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    PaymentRepository paymentRepository;
    PaymentMapper paymentMapper;
    UserService userService;
    CompanyRepository companyRepository;


    public Payment save(Payment payment){
        return paymentRepository.save(payment);
    }

    public Payment createNew(User user, Company company){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime period = getPeriod();
        Payment payment = Payment.builder()
                .user(user)
                .company(company)
                .totalEnergy(BigDecimal.ZERO)
                .totalCost(BigDecimal.ZERO)
                .status(PaymentStatus.UNPAID)
                .period(period)
                .invoices(new ArrayList<>())
                .paymentTransactions(new ArrayList<>())
                .build();
        return payment;
    }

    public PaymentResponse createPayment(PaymentCreationRequest request) {
        Payment payment = null;
        //Check user có gói chưa
        if(request.getUserId() != null ) {
            User user = userService.findById(request.getUserId());
            if(!user.getPayments().stream().filter(paymentCheck ->
                    paymentCheck.getPeriod().equals(getPeriod())).findFirst().isEmpty()) {
                payment = createNew(user, null);
            }else{
                throw new AppException(ErrorCode.RESOURCES_EXISTS,"Payment");
            }
        }
        //Check company
        if(request.getCompanyId() != null ) {
            Company company = companyRepository.findById(request.getCompanyId()).orElse(null);
            if(company.getPayment().stream().filter(paymentCheck ->
                    paymentCheck.getPeriod().equals(getPeriod())).findFirst().isEmpty()){
                payment = createNew(null, company);
            }else{
                throw new AppException(ErrorCode.RESOURCES_EXISTS,"Payment");
            }
        }
        if(payment==null) throw new RuntimeException("Payment must not be null");

        Payment saved = paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(saved);
    }

    public Payment findByUser(User user){
        final LocalDateTime finalPeriod = getPeriod();
        Payment payment = null;
        Company company = user.getCompany();
        if(company!=null){
            payment = paymentRepository.findByCompanyAndPeriod(company,finalPeriod);
        }else{
            payment = paymentRepository.findByUserAndPeriod(user,finalPeriod);
        }
        return payment;
    }

    public LocalDateTime getPeriod(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime period = LocalDateTime.of(now.getYear(), now.getMonth(), 25, 0, 0);
        if (now.getDayOfMonth() > 25) {
            period = period.plusMonths(1);
        }
        return period;
    }


    public List<PaymentResponse> getAll() {

    }

    public List<PaymentResponse> getByCompany(Long id) {
    }

    public List<PaymentResponse> getByUser(Long id) {
    }
}
