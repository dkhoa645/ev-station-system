package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.request.VehicleRequest;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);
    UserResponse toUserResponse(User user);
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);

}
