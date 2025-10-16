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
    private Integer userId;
    private String userName;
    private Integer stationId;
    private String stationName;
    private Integer spotId;
    private String spotName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
