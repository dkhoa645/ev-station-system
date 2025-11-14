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
public class StartRequestForStaff {
    @Schema(description = "ID của spot tại trạm", example = "5", required = true)
    @NotNull(message = "spotId is required")
    private Long spotId;

    @Schema(description = "Lượng pin ban đầu", example = "5", required = true)
    @NotNull(message = "percentBefore is required")
    private Double percentBefore;
}
