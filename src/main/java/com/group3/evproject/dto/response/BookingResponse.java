package com.group3.evproject.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Integer bookingId;
    private Long userId;
    private String userName;
    private Long stationId;
    private String stationName;
    private Long spotId;
    private String spotName;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
