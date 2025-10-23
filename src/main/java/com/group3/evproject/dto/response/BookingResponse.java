package com.group3.evproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin trả về sau khi người dùng đặt chỗ thành công")
public class BookingResponse {

    @Schema(description = "ID của lượt đặt chỗ", example = "101")
    private Long bookingId;

    @Schema(description = "Tên trạm sạc", example = "Trạm sạc VinFast Nha Trang Center")
    private String stationName;

    @Schema(description = "Thời gian người dùng thực hiện booking")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;

    @Schema(description = "Thời điểm bắt đầu sạc")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeToCharge;

    @Schema(description = "Thời điểm kết thúc sạc")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "Trạng thái của booking", example = "PENDING")
    private String status;
}
