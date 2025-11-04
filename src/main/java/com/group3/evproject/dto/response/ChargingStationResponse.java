package com.group3.evproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Thông tin trả về sau khi người dùng đặt chỗ thành công")

public class ChargingStationResponse {

    @Schema(description = "ID của station", example = "101")
    private Long stationId;

    @Schema(description = "Tên của trạm sạc", example = "VinFast Charging Station")
    private String stationName;

    @Schema(description = "Địa chỉ trạm sạc", example = "123 Đường 100, TP Thủ Đức, TP HCM")
    private String location;

    @Schema(description = "Trạng thái", example = "AVAILABLE")
    private String status;

    @Schema(description = "Hình ảnh trạm sạc", example = "https://exmaple.jpg")
    private String imageUrl;

    @Schema(description = "Tổng trụ sạc dành cho vãng lai", example = "5")
    private Integer totalSpotsOfline;

    @Schema(description = "Tổng trụ sạc dành cho booking", example = "5")
    private Integer totalSpotsOnline;

    @Schema(description = "Tổng trụ sạc gồm booking + vãng lai", example = "10")
    private Integer totalSpots;

    @Schema(description = "Công suất trụ sạc", example = "180")
    private Double powerCapacity;

    @Schema(description = "Vị trí kinh độ", example = "10.7945")
    private Double latitude;

    @Schema(description = "Vị trí vĩ độ", example = "106.7945")
    private Double longtitude;

}
