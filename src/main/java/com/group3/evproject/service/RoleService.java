package com.group3.evproject.service;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.entity.Role;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    public Role findByName(RoleName role) {
        return roleRepository.findByName(role)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Role"));
    }
}
