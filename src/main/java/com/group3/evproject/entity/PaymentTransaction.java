package com.group3.evproject.entity;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //  Mỗi payment thuộc về 1 gói đăng ký xe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_subscription_id")
    private VehicleSubscription vehicleSubscription;
    // Số tiền thanh toán
    private BigDecimal amount;
    //  Cổng hoặc phương thức thanh toán (VD: VNPAY, MOMO, CREDIT_CARD)
    private String paymentMethod;
    // Mã giao dịch duy nhất (vnp_TxnRef)
    @Column(unique = true, nullable = false)
    private String vnpTxnRef;
    // Trạng thái giao dịch (FAILED, SUCCESS)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatusEnum status;
    //  Thời điểm thanh toán thành công (nếu có)
    private LocalDateTime paidAt;
    //  Thông tin mô tả ngắn (tùy chọn)
    private String orderInfo;
    //  Mã ngân hàng (VNPAY callback)
    private String bankCode = "VNBANK";

}
