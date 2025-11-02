package com.group3.evproject.vnpay;

import com.group3.evproject.utils.VNPayUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

import lombok.NoArgsConstructor;

@Configuration
@NoArgsConstructor
@Data
@AllArgsConstructor
public class VNPayConfig {
    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;
    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode ;
    @Value("${vnpay.hash-secret}")
    private String secretKey;
    @Value("${vnpay.version}")
    private String vnp_Version;
    @Value("${vnpay.command}")
    private String vnp_Command;
    @Value("${vnpay.order-type}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
//        vnpParamsMap.put("vnp_TxnRef",  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" +  VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);

        TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(tz);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(tz);
        String vnpCreateDate = formatter.format(calendar.getTime());

        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}
