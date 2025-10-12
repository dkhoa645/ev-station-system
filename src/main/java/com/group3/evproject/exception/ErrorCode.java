package com.group3.evproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USERNAME_INVALID(1001,"Username must have at least {min} characters" , HttpStatus.BAD_REQUEST),
    USERNAME_EXISTS(1002,"Username is already exist" , HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003,"Password must have at least {min} characters" , HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(1004,"Email is already exist" , HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EXISTS(1005,"Username not exist" , HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    VEHICLE_NOT_EXISTS(1008, "Vehicle does not exist", HttpStatus.BAD_REQUEST),
    VEHICLE_EXISTS(1009, "Vehicle already exist", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(1010,"Email is not verified" , HttpStatus.BAD_REQUEST),
    EMAIL_VERIFIED(1011,"Username is exist and verified" , HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1012,"Token is invalid" , HttpStatus.BAD_REQUEST)

    ;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;
}
