package com.group3.evproject.service;

import com.group3.evproject.dto.request.SubscriptionPlanRequest;
import com.group3.evproject.dto.response.SubscriptionPlanResponse;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.SubscriptionPlanMapper;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPlanService {
    SubscriptionPlanRepository subscriptionPlanRepository;
    SubscriptionPlanMapper mapper;
    private final SubscriptionPlanMapper subscriptionPlanMapper;

    public SubscriptionPlan savePlan(SubscriptionPlan sp){
        return subscriptionPlanRepository.save(sp);
    }

    public List<SubscriptionPlanResponse> getAll() {
        List<SubscriptionPlanResponse> subscriptionPlans = new ArrayList<>();
        for(SubscriptionPlan subscriptionPlan : subscriptionPlanRepository.findAll()) {
            subscriptionPlans.add(mapper.toSubscriptionPlanResponse(subscriptionPlan));
        }
        return subscriptionPlans;
    }

    public SubscriptionPlan findById(Long id){
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription plan"));
    }

    @Transactional
    public SubscriptionPlan createPlan(SubscriptionPlanRequest subscriptionPlanRequest) {
        SubscriptionPlan subscriptionPlan = new SubscriptionPlan();
        mapper.toSubscriptionPlan(subscriptionPlanRequest, subscriptionPlan);

        // discount là Double (phần trăm)
        if (subscriptionPlan.getDiscount() != null) {
            subscriptionPlan.setMultiplier(1.0 - subscriptionPlan.getDiscount() / 100.0);
        } else {
            subscriptionPlan.setMultiplier(1.0);
        }

        return subscriptionPlanRepository.save(subscriptionPlan);
    }


    @Transactional
    public SubscriptionPlan updatePlan(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));
        mapper.toSubscriptionPlan(request,existing);
        return subscriptionPlanRepository.save(existing);
    }
    @Transactional
    public void deleteById(Long id) {
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));
        subscriptionPlanRepository.delete(existing);
    }

    public SubscriptionPlanResponse getById(Long id) {
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));

        return subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlan);
    }
}
