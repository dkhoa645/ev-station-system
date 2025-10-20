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
public class BookingRequest {

    @Schema(description = "ID of the user making the booking", example = "1")
    @NotNull
    private Long userId;

    @Schema(description = "ID of the charging station", example = "1")
    @NotNull
    private Integer stationId;

    @Schema(description = "Optional ID of a specific charging spot", example = "1")
    private Integer spotId; // optional - server can pick available if null

    @Schema(description = "Start time of the booking in ISO 8601 format", example = "2023-10-01T10:00:00")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "End time of the booking in ISO 8601 format", example = "2023-10-01T12:00:00")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
}
