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
public class EndRequest {
    @Schema(description = "Giá sạc", example = "5", required = true)
    @NotNull(message = "ratePerKWh is required")
    private Double ratePerKWh;

    @Schema(description = "Dung lượng pin xe", example = "10", required = true)
    @NotNull(message = "batteryCapacity is required")
    private Double batteryCapacity;

    @Schema(description = "Dung lượng pin của xe trước khi sạc", example = "20", required = true)
    @NotNull(message = "percentBefore is required")
    private Double percentBefore;
}
