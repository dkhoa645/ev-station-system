package com.group3.evproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyUpdateRequest {
    String address;
    String contactEmail;
    String contactName;
}
