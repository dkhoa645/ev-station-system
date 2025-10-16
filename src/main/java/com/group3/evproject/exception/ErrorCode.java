package com.group3.evproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
//    Validation
    USERNAME_INVALID(1001,"Username must have at least {min} characters" , HttpStatus.BAD_REQUEST),
    RESOURCES_EXISTS(1002,"{0} is already exist" , HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003,"Password must have at least {min} characters" , HttpStatus.BAD_REQUEST),
    RESOURCES_NOT_EXISTS(1004,"{0} not exist" , HttpStatus.BAD_REQUEST),
//        Security
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    EMAIL_NOT_VERIFIED(1010,"Email is not verified" , HttpStatus.BAD_REQUEST),
    EMAIL_VERIFIED(1011,"Username is exist and verified" , HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1012,"Token is invalid" , HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_REQUIRED(1013,"Subscription required" , HttpStatus.BAD_REQUEST),
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
