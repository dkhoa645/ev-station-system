package com.group3.evproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin khi người dùng đặt lịch sạc (booking request)")
public class BookingRequest {

    @Schema(description = "ID của trạm sạc mà người dùng muốn đặt", example = "1", required = true)
    @NotNull(message = "stationId is required")
    private Integer stationId;

    @Schema(description = "Thời điểm bắt đầu sạc (user muốn bắt đầu sạc)",example = "2025-10-25T19:00:00", required = true)
    @NotNull(message = "timeToCharge is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeToCharge;

    @Schema(description = "Thời điểm kết thúc sạc dự kiến",example = "2025-10-25T21:00:00", required = true)
    @NotNull(message = "endTime is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
}
