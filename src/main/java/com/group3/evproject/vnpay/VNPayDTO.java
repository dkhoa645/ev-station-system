package com.group3.evproject.vnpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayDTO {

        public String code;
        public String message;
        public String paymentUrl;
}
