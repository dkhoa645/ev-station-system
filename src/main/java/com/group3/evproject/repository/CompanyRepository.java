package com.group3.evproject.repository;

import com.group3.evproject.entity.Company;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {
    boolean existByContactEmail(
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email wrong format")
            String contactEmail);

    boolean existByName(@Size(min = 1, max = 50) String name);
}
