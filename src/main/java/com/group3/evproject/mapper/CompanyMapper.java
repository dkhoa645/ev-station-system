package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.CompanyCreationRequest;
import com.group3.evproject.dto.request.CompanyUpdateRequest;
import com.group3.evproject.dto.response.CompanyResponse;
import com.group3.evproject.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    @Mapping(target = "user",ignore = true)
    Company toCompany(CompanyCreationRequest request);

    CompanyResponse toCompanyResponse(Company company);

    void updateCompany(CompanyUpdateRequest companyUpdateRequest, @MappingTarget Company company);
}
