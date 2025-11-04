package com.group3.evproject.controller;

import com.group3.evproject.dto.request.CompanyCreationRequest;
import com.group3.evproject.dto.request.CompanyUpdateRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.CompanyResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.service.CompanyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/company")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    @GetMapping
    public ApiResponse<List<CompanyResponse>> getAll(){
        return ApiResponse.<List<CompanyResponse>>builder()
                .result(companyService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getOne(@PathVariable Long id){
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.getById(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<CompanyResponse> createCompany(@RequestBody CompanyCreationRequest companyCreationRequest) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.createCompany(companyCreationRequest))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> updateCompany(
            @RequestBody CompanyUpdateRequest companyUpdateRequest,
            @PathVariable Long id
    ) {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.updateCompany(id,companyUpdateRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCompany(@PathVariable Long id){
        return ApiResponse.<String>builder()
                .result(companyService.deleteCompany(id))
                .build();
    }
}
