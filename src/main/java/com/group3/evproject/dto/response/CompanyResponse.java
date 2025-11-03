package com.group3.evproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyResponse {
    Long id;
    String name;
    String address;
    String contactEmail;
}
