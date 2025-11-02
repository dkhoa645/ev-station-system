package com.group3.evproject.controller.admin;

import com.group3.evproject.dto.request.SubscriptionPlanRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.SubscriptionPlanResponse;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.service.SubscriptionPlanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/subscription-plan")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPlanController {
    SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ApiResponse<List<SubscriptionPlanResponse>> getAllSubscriptionPlans() {
        return ApiResponse.<List<SubscriptionPlanResponse>>builder()
                .result(subscriptionPlanService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SubscriptionPlan> getSubscriptionPlanById(@PathVariable Long id) {
        return ApiResponse.<SubscriptionPlan>builder()
                .result(subscriptionPlanService.getById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<SubscriptionPlan> createSubscriptionPlan(
            @RequestBody SubscriptionPlanRequest subscriptionPlanRequest) {
        return ApiResponse.<SubscriptionPlan>builder()
                .result(subscriptionPlanService.createPlan(subscriptionPlanRequest))
                .build();
    }
//
    @PutMapping("/{id}")
    public ApiResponse<SubscriptionPlan> updateSubscriptionPlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanRequest request) {
        return ApiResponse.<SubscriptionPlan>builder()
                .result(subscriptionPlanService.updatePlan(id,request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSubscriptionPlan(
            @PathVariable Long id
    ){
        subscriptionPlanService.deleteById(id);
        return ApiResponse.<String>builder()
                .result("Subscription Plan has been deleted")
                .build();
    }
}
