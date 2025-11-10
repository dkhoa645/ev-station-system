package com.group3.evproject.service;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.dto.request.CompanyCreationRequest;
import com.group3.evproject.dto.request.CompanyUpdateRequest;
import com.group3.evproject.dto.response.CompanyResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.entity.Role;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.CompanyMapper;
import com.group3.evproject.repository.CompanyRepository;

import com.group3.evproject.utils.PasswordUntil;
import com.group3.evproject.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;
    CompanyMapper companyMapper;
    PaymentService paymentService;
    UserService userService;
    RoleService roleService;
    EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserUtils userUtils;

    @Transactional
    public CompanyResponse createCompany(CompanyCreationRequest companyCreationRequest) {
        if(companyRepository.existsByContactEmail(companyCreationRequest.getContactEmail()))
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Company Email");
        if(companyRepository.existsByName(companyCreationRequest.getName()))
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Company Name");

        Company company = companyMapper.toCompany(companyCreationRequest);

        Payment payment = paymentService.createNew(null,company);
        List<Payment> list = new ArrayList<>();
                list.add(payment);
        company.setPayment(list);

//        Tạo tài khoản cho company luôn
        Role companyRole = roleService.findByName(RoleName.COMPANY);
        Set<Role> roles = new HashSet<>();
        roles.add(companyRole);

        String password = PasswordUntil.generateSecurePassword(8);

        emailService.sendCompanyEmail(company.getContactEmail(), password);

        if(userService.getByEmail(company.getContactEmail())!=null){
            throw  new AppException(ErrorCode.RESOURCES_EXISTS,"Email");
        }

        User user = User.builder()
                .username(company.getContactEmail())
                .email(company.getContactEmail())
                .password(passwordEncoder.encode(password))
                .name(company.getName())
                .roles(roles)
                .company(company)
                .verified(true)
                .build();

        userService.save(user);
        List<User> users = new ArrayList<>();
        users.add(user);
        company.setUser(users);

        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    public List<CompanyResponse> getAll() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toCompanyResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse getById(Long id) {
        return companyMapper.toCompanyResponse(companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company")));
    }

    @Transactional
    public CompanyResponse updateCompany(Long id ,CompanyUpdateRequest companyUpdateRequest) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company"));
        companyMapper.updateCompany(companyUpdateRequest,company);
        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    @Transactional
    public String deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company"));

        companyRepository.delete(company);
        return "Company "+ company.getName() + " has been deleted";
    }

    public Company findById(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    public CompanyResponse getCompanyInfo() {
        User user = userUtils.getCurrentUser();
        return companyMapper.toCompanyResponse(companyRepository.findById(user.getCompany().getId())
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company")));
    }
}
