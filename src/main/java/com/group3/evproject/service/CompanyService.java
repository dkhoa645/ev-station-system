package com.group3.evproject.service;

import com.group3.evproject.dto.request.CompanyCreationRequest;
import com.group3.evproject.dto.request.CompanyUpdateRequest;
import com.group3.evproject.dto.response.CompanyResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.CompanyMapper;
import com.group3.evproject.repository.CompanyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;
    CompanyMapper companyMapper;

    public CompanyResponse createCompany(CompanyCreationRequest companyCreationRequest) {
        if(companyRepository.existByContactEmail(companyCreationRequest.getContactEmail()))
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Company Email");
        if(companyRepository.existByName(companyCreationRequest.getName()))
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Company Name");
        Company company = companyMapper.toCompany(companyCreationRequest);
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

    public CompanyResponse updateCompany(Long id ,CompanyUpdateRequest companyUpdateRequest) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company"));
        companyMapper.updateCompany(companyUpdateRequest,company);
        return companyMapper.toCompanyResponse(companyRepository.save(company));
    }

    public String deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company"));
        companyRepository.delete(company);
        return "Company "+ company.getName() + " Deleted";
    }
}
