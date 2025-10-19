package com.group3.evproject.service;

import com.group3.evproject.dto.request.AuthenticationRequest;
import com.group3.evproject.dto.request.IntrospectRequest;
import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.response.AuthenticationResponse;
import com.group3.evproject.dto.response.IntrospectResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.Role;
import com.group3.evproject.entity.RoleEnum;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.repository.RoleRepository;
import com.group3.evproject.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    RoleRepository roleRepository;
    UserRepository userRepository;
    EmailService emailService;

    private PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY =
            "WgHn5RfQOLDuYzYwVs9rqFfD7wvDRJxa7JjjiMa1VmL/3pl2o2H/vdynTH+DmtfE";

    public AuthenticationResponse authenticated(AuthenticationRequest request) {
        var user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "User"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        boolean isUserRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("USER"));

        if (!user.isVerified()) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        var token = generateToken(request.getUsername());

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();


    }


    private String generateToken(String username) {
//        Tao header voi thuat toan
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//            Tao Claimset cho vao Payload
        Map<String, Object> userInfo = new HashMap<>();
        User user = userRepository.findByUsername(username).orElse(null);
        userInfo.put("email", user.getEmail());
        userInfo.put("name", user.getName());
        userInfo.put("password", user.getPassword());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("backend-dev")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .claim("user", userInfo)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);


        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {

        var token = request.getToken();
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verified && expityTime.after(new Date()))
                .build();
    }

    @Transactional
    public UserResponse registerUser(UserCreationRequest request) {
        // 1. Kiểm tra email tồn tại
        User existUser = userRepository.findByEmail(request.getEmail());
        if (existUser != null) {
            if (existUser.isVerified()) {
                throw new AppException(ErrorCode.EMAIL_VERIFIED);
            } else {
//           *Tồn tại nhưng chưa xác thực
                String verificationToken = UUID.randomUUID().toString();
                existUser.setVerificationToken(verificationToken);
                userRepository.save(existUser);
                emailService.sendVerificationEmail(existUser.getEmail(), verificationToken);
                return UserResponse.builder()
                        .id(existUser.getId())
                        .email(existUser.getEmail())
                        .username(existUser.getUsername())
                        .message("Email resent!!Please check your email to verify your account.")
                        .build();
            }
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"User");
        }
        // 2. Tạo User mới
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);
        // 3. Gán role mặc định USER
//        Role role = roleRepository.findByName("USER");
//        user.getRoles().add(role);
        // 4. Sinh token xác minh email
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        // 5. Gửi email xác thực
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        // 6. Trả về DTO
        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setMessage("Registration successfull! Please Verify your Email");
        return userResponse;
    }

    @Transactional
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken((token));
        if (user == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return "Email verified successfully! You can now login.";
    }


    public String extractUsernameFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.TOKEN_INVALID); // hoặc TOKEN_MISSING
        }
        String token = authHeader.substring(7);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }
}
