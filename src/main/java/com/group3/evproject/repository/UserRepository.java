package com.group3.evproject.repository;

import com.group3.evproject.dto.response.CompanyUserResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.Company;
import com.group3.evproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    User findByEmail(String email);
    User findByVerificationToken(String verificationToken);
    Optional<User> findByCompanyIdAndIdNot(Long id, Long id1);
    List<User> findByCompanyAndIdNot(Company company, Long id);
}
