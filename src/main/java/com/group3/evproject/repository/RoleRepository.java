package com.group3.evproject.repository;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
     Optional<Role> findByName(RoleName roleName);

}
