package com.group3.evproject.config;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.entity.Role;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.repository.RoleRepository;
import com.group3.evproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner initApplicationRunner(UserRepository userRepository,RoleRepository roleRepository) {
        return args -> {
            //  1. Tạo các role nếu chưa có
            Arrays.stream(RoleName.values()).forEach(roleName -> {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setName(roleName);
                            return roleRepository.save(role);
                        });
            });
            //  2. Tạo ADMIN nếu chưa có
           Role admin = roleRepository.findByName(RoleName.ROLE_ADMIN)
                   .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Role"));
          if(userRepository.findByUsername("admin").isEmpty()) {
              Set<Role> roles = new HashSet<>();
              roles.add(admin);
              User adminAccount = User.builder()
                      .username("admin")
                      .password(passwordEncoder.encode("admin"))
                      .email("admin@gmail.com")
                      .verified(true)
                      .roles(roles)
                      .build();
              userRepository.save(adminAccount);
          }

            log.warn("admin user has been created with default password: admin, please change it");
        };
    }
}
