package com.group3.evproject.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    @Schema(description = "ID of the user making the booking", example = "1")
    private Integer userId;

    @Schema(description = "ID of the vehicle to be charged", example = "1")
    private Integer vehicleId;

    @Schema(description = "ID of the charging station", example = "1")
    private Integer stationId;

    @Schema(description = "Start time of the booking in ISO 8601 format", example = "2023-10-01T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "End time of the booking in ISO 8601 format", example = "2023-10-01T12:00:00")
    private LocalDateTime endTime;
}
