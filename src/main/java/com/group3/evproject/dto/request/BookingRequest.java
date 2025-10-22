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
@Schema(description = "Information sent when users book charging slots")
public class BookingRequest {

    @Schema(description = "ID of the user making the booking", example = "1", required = true)
    @NotNull(message = "station is required")
    private Integer userId;

    @Schema
    @NotNull
    private Integer stationId;

    @Schema(description = "Start time of the booking in ISO 8601 format", example = "2023-10-01T10:00:00", required = true)
    @NotNull(message = "startTime is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "End time of the booking in ISO 8601 format", example = "2023-10-01T12:00:00",required = true)
    @NotNull(message = "endTime is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
}
