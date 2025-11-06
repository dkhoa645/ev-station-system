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
public class StartRequest {
    @Schema(description = "ID của spot tại trạm", example = "5", required = true)
    @NotNull(message = "spotId is required")
    private Long spotId;

}
