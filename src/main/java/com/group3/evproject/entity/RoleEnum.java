package com.group3.evproject.entity;

import lombok.Data;
import lombok.Getter;

@Getter
public enum RoleEnum {
    USER("USER"),
    ADMIN("ADMIN"),
    STAFF("STAFF")
    ;

    RoleEnum(String value) {
        this.value = value;
    }
    private String value;
}
