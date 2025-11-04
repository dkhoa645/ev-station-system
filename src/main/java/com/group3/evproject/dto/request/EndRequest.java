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
    @Schema(description = "ID của phiên sạc", example = "5", required = true)
    @NotNull(message = "sesionId is required")
    private Long sessionId;

    @Schema(description = "Giá sạc", example = "5", required = true)
    @NotNull(message = "ratePerKWh is required")
    private Double ratePerKWh;

}
