package com.group3.evproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class StartRequestForMember {
    @Schema(description = "ID của spot tại trạm", example = "5", required = true)
    @NotNull(message = "spotId is required")
    private Long spotId;

    @Schema(description = "ID của người dùng", example = "5", required = true)
    @NotNull(message = "userId is required")
    private Long userId;

    @Schema(description = "ID của xe", example = "5", required = true)
    @NotNull(message = "vehicleId is required")
    private Long vehicleId;

    @Schema(description = "ID của trạm sạc", example = "5", required = true)
    @NotNull(message = "stationId is required")
    private Long stationId;

    @Schema(description = "Lượng pin ban đầu", example = "5", required = true)
    @NotNull(message = "percentBefore is required")
    private Double percentBefore;;
}
