package com.group3.evproject.service;

import com.group3.evproject.dto.request.SubscriptionPlanRequest;
import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.SubscriptionPlanMapper;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionPlanService {
    SubscriptionPlanRepository subscriptionPlanRepository;
    SubscriptionPlanMapper mapper;

    public List<SubscriptionPlan> getAll() {
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan createPlan(SubscriptionPlan subscriptionPlan) {
        return subscriptionPlanRepository.save(subscriptionPlan);
    }

    public SubscriptionPlan updatePlan(Long id, SubscriptionPlanRequest request) {
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));
        mapper.toSubscriptionPlan(request,existing);
        return subscriptionPlanRepository.save(existing);
    }

    public void deleteById(Long id) {
        SubscriptionPlan existing = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));
        subscriptionPlanRepository.delete(existing);
    }

    public SubscriptionPlan getById(Long id) {
        return subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription"));
    }
}
