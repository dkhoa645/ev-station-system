package com.group3.evproject.controller.member;

import com.group3.evproject.dto.request.SubscriptionRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.dto.response.VehicleSubscriptionResponse;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.service.SubscriptionPlanService;
import com.group3.evproject.service.VehicleSubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member/vehicle-subscription")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleSubscriptionController {

        VehicleSubscriptionService vehicleSubscriptionService;

        @PutMapping("/expired")
        public ApiResponse<VehicleResponse> expireSubscription(
                @PathVariable Long vehicleId
        ) {
            return ApiResponse.<VehicleResponse>builder()
                    .result(vehicleSubscriptionService.expiredSubscription(vehicleId))
                    .build();
        }

        @PutMapping("/no-renew/{vehicleId}")
        public ApiResponse<String> noRenewSubscription(
            @PathVariable Long vehicleId
        ){
            return ApiResponse.<String>builder()
                    .result(vehicleSubscriptionService.noRenewSubscription(vehicleId))
                    .build();
        }


        @PutMapping("/renew")
        public ApiResponse<VehicleResponse> renewSubscription(
                @RequestBody SubscriptionRequest subscriptionRequest
                ){
            return ApiResponse.<VehicleResponse>builder()
                    .result(vehicleSubscriptionService.renewSubscription(subscriptionRequest))
                    .build();
        }



}
