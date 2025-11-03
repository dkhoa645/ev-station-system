package com.group3.evproject.dto.response;

import com.group3.evproject.entity.ChargingSession;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSessionSimpleResponse {

    Long sessionId;
    String stationName;
    String spotName;
    Long bookingId;
    LocalDateTime startTime;
    String status;

}
