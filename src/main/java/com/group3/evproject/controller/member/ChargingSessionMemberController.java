package com.group3.evproject.controller.member;


import com.group3.evproject.dto.request.*;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.mapper.ChargingSessionMapper;
import com.group3.evproject.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/sessions")
@RequiredArgsConstructor
@CrossOrigin("*")

public class ChargingSessionMemberController {
    private final ChargingSessionService chargingSessionService;

    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody StartRequestForMember startRequestForMember) {
        try {
            Long spotId = startRequestForMember.getSpotId();
            Double percentBefore = startRequestForMember.getPercentBefore();
            if (spotId == null || percentBefore == null) {
                return ResponseEntity.badRequest().body("Missing required field: spotId, percentBefore");
            }

            ChargingSession session = chargingSessionService.startSessionForMember(spotId, percentBefore);
            return ResponseEntity.ok(ChargingSessionMapper.toSimpleResponse(session));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error starting session: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/end/{session_id}")
    public ResponseEntity<?> endSession(
            @PathVariable("session_id") Long sessionId,
            @RequestBody EndRequestForMember endRequestForMember) {
        try {
            Double ratePerKWh = endRequestForMember.getRatePerKWh();
            Double batteryCapacity = endRequestForMember.getBatteryCapacity();
            Double percentBefore = endRequestForMember.getPercentBefore();

            if (ratePerKWh == null || batteryCapacity == null) {
                return ResponseEntity.badRequest().body("Missing required fields: ratePerKWh");
            }

            ChargingSession session = chargingSessionService.endSessionForMember(batteryCapacity ,ratePerKWh, sessionId, percentBefore);

            //Dùng Mapper để trả về DTO đầy đủ
            return ResponseEntity.ok(ChargingSessionMapper.toDetailResponse(session));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error ending session: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }


}
