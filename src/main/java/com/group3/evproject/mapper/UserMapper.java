package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.AdminUserCreationRequest;
import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);

    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);

    @Mapping(target="roles",ignore = true)
    User toUserFormAdmin(AdminUserCreationRequest adminUserCreationRequest);

}
