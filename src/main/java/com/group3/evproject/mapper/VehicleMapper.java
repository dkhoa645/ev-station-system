package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    @Mapping(source = "model", target = "model")
    VehicleResponse vehicleToVehicleResponse(Vehicle vehicle);


//    @Mapping(source = "userId", target = "user", qualifiedByName = "mapUser")
//    @Mapping(source = "companyId", target = "company", qualifiedByName = "mapCompany")
//    Vehicle toVehicle(VehicleRequest vehicleRequest);
//    VehicleResponse toVehicleResponse(Vehicle vehicle);
//
//
//    @Named("mapUser")
//    default User mapUser(Long userId) {
//        if (userId == null) return null;
//        User user = new User();
//        user.setId(userId);
//        return user;
//    }
//    @Named("mapCompany")
//    default Company mapCompany(Integer companyId) {
//        if (companyId == null) return null;
//        Company company = new Company();
//        company.setId(companyId);
//        return company;
//    }
}
