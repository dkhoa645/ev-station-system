package com.group3.evproject.repository;

import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v JOIN FETCH v.subscription")
    List<Vehicle> findAllWithSubscription();

    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByUser(User user);
}
